package com.aatzhmair.orderservice.service;

import com.aatzhmair.orderservice.dto.OrderResponse;
import com.aatzhmair.orderservice.dto.PlaceOrderRequest;
import com.aatzhmair.orderservice.entity.Order;
import com.aatzhmair.orderservice.exception.OrderNotFoundException;
import com.aatzhmair.orderservice.repository.OrderRepository;
import com.aatzhmair.orderservice.saga.OrderSagaOrchestrator;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderSagaOrchestrator sagaOrchestrator;

    public OrderService(OrderRepository orderRepository, OrderSagaOrchestrator sagaOrchestrator) {
        this.orderRepository = orderRepository;
        this.sagaOrchestrator = sagaOrchestrator;
    }

    public OrderResponse placeOrder(UUID userId, String idempotencyKey, PlaceOrderRequest request) {
        Order existing = orderRepository.findByIdempotencyKey(idempotencyKey).orElse(null);
        if (existing != null) {
            return toResponse(existing);
        }

        Order order = new Order(userId, idempotencyKey);
        orderRepository.save(order);
        return toResponse(sagaOrchestrator.execute(order, request));
    }

    public OrderResponse getById(UUID id) {
        return orderRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private OrderResponse toResponse(Order order) {
        var lineItems = order.getLineItems().stream()
                .map(li -> new OrderResponse.LineItemResponse(li.getProductId(), li.getQuantity(), li.getUnitPrice()))
                .toList();
        return new OrderResponse(order.getId(), order.getStatus().name(), order.getTotalAmount(),
                order.getFailureReason(), lineItems, order.getCreatedAt());
    }
}