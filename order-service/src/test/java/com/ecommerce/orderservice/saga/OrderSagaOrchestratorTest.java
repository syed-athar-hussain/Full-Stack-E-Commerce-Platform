package com.aatzhmair.orderservice.saga;

import com.aatzhmair.common.events.OrderPlacedEvent;
import com.aatzhmair.orderservice.client.ProductServiceClient;
import com.aatzhmair.orderservice.dto.PlaceOrderRequest;
import com.aatzhmair.orderservice.dto.ProductResponse;
import com.aatzhmair.orderservice.dto.StockAdjustmentRequest;
import com.aatzhmair.orderservice.entity.Order;
import com.aatzhmair.orderservice.exception.OrderFailedException;
import com.aatzhmair.orderservice.exception.PaymentFailedException;
import com.aatzhmair.orderservice.payment.StripePaymentService;
import com.aatzhmair.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderSagaOrchestratorTest {

    @Mock ProductServiceClient productServiceClient;
    @Mock StripePaymentService stripePaymentService;
    @Mock OrderRepository orderRepository;
    @Mock KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Test
    void execute_restoresStockForEveryReservedItemWhenPaymentFails() {
        var orchestrator = new OrderSagaOrchestrator(productServiceClient, stripePaymentService, orderRepository, kafkaTemplate);

        UUID productA = UUID.randomUUID();
        UUID productB = UUID.randomUUID();
        var request = new PlaceOrderRequest(List.of(
                new PlaceOrderRequest.LineItem(productA, 2),
                new PlaceOrderRequest.LineItem(productB, 1)));
        var order = new Order(UUID.randomUUID(), "idem-key-1");

        when(productServiceClient.getById(productA)).thenReturn(
                new ProductResponse(productA, "A", "desc", BigDecimal.TEN, "cat", 10, null));
        when(productServiceClient.getById(productB)).thenReturn(
                new ProductResponse(productB, "B", "desc", BigDecimal.valueOf(5), "cat", 10, null));
        when(stripePaymentService.charge(any(), any())).thenThrow(new PaymentFailedException("card declined", null));

        assertThatThrownBy(() -> orchestrator.execute(order, request)).isInstanceOf(OrderFailedException.class);

        verify(productServiceClient).restoreStock(productA, new StockAdjustmentRequest(2));
        verify(productServiceClient).restoreStock(productB, new StockAdjustmentRequest(1));
        verify(kafkaTemplate, never()).send(any(), any(), any());
    }
}