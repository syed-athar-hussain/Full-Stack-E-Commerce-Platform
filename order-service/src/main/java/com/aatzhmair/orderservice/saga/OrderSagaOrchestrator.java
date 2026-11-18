package com.aatzhmair.orderservice.saga;

import com.aatzhmair.common.events.OrderPlacedEvent;
import com.aatzhmair.orderservice.client.ProductServiceGateway;
import com.aatzhmair.orderservice.dto.PlaceOrderRequest;
import com.aatzhmair.orderservice.dto.ProductResponse;
import com.aatzhmair.orderservice.dto.StockAdjustmentRequest;
import com.aatzhmair.orderservice.entity.Order;
import com.aatzhmair.orderservice.entity.OrderLineItem;
import com.aatzhmair.orderservice.exception.OrderFailedException;
import com.aatzhmair.orderservice.payment.StripePaymentService;
import com.aatzhmair.orderservice.repository.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class OrderSagaOrchestrator {

    private final ProductServiceGateway productServiceGateway;
    private final StripePaymentService stripePaymentService;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final MeterRegistry meterRegistry;

    public OrderSagaOrchestrator(ProductServiceGateway productServiceGateway,
                                  StripePaymentService stripePaymentService,
                                  OrderRepository orderRepository,
                                  KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.productServiceGateway = productServiceGateway;
        this.stripePaymentService = stripePaymentService;
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    private record Reserved(UUID productId, int quantity, BigDecimal unitPrice) {}

    public Order execute(Order order, PlaceOrderRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);
        List<Reserved> reserved = new ArrayList<>();

        try {
            for (var item : request.lineItems()) {
                ProductResponse product = productServiceGateway.getById(item.productId()); // price comes from here, never from the client
                productServiceGateway.decreaseStock(item.productId(), new StockAdjustmentRequest(item.quantity()));
                reserved.add(new Reserved(item.productId(), item.quantity(), product.price()));
            }

            BigDecimal total = reserved.stream()
                    .map(r -> r.unitPrice().multiply(BigDecimal.valueOf(r.quantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String paymentIntentId = stripePaymentService.charge(total, order.getIdempotencyKey());

            reserved.forEach(r -> order.addLineItem(new OrderLineItem(r.productId(), r.quantity(), r.unitPrice())));
            order.confirm(paymentIntentId, total);
            orderRepository.save(order);

            kafkaTemplate.send("order-events", order.getId().toString(), toEvent(order));
            meterRegistry.counter("orders.placed", "status", "confirmed").increment();
            sample.stop(meterRegistry.timer("orders.saga.duration", "status", "confirmed"));
            return order;

        } catch (Exception e) {
            for (Reserved r : reserved) {
                try {
                    productServiceGateway.restoreStock(r.productId(), new StockAdjustmentRequest(r.quantity()));
                } catch (Exception compensationFailure) {
                    log.error("Failed to restore stock for product {} while compensating order {}",
                            r.productId(), order.getId(), compensationFailure);
                }
            }
            order.fail(e.getMessage());
            orderRepository.save(order);
            meterRegistry.counter("orders.placed", "status", "failed").increment();
            sample.stop(meterRegistry.timer("orders.saga.duration", "status", "failed"));
            throw new OrderFailedException(order.getId(), e.getMessage(), e);
        }
    }

    private OrderPlacedEvent toEvent(Order order) {
        var lineItems = order.getLineItems().stream()
                .map(li -> new OrderPlacedEvent.OrderLineItem(li.getProductId(), li.getQuantity(), li.getUnitPrice()))
                .toList();
        return new OrderPlacedEvent(order.getId(), order.getUserId(), lineItems, order.getTotalAmount(), Instant.now());
    }
}