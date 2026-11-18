package com.aatzhmair.orderservice.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "order_schema")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(name = "failure_reason")
    private String failureReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineItem> lineItems = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Order() {}

    public Order(UUID userId, String idempotencyKey) {
        this.userId = userId;
        this.idempotencyKey = idempotencyKey;
        this.status = OrderStatus.PENDING;
    }

    public void confirm(String paymentIntentId, BigDecimal totalAmount) {
        this.status = OrderStatus.CONFIRMED;
        this.paymentIntentId = paymentIntentId;
        this.totalAmount = totalAmount;
        this.updatedAt = Instant.now();
    }

    public void fail(String reason) {
        this.status = OrderStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
    }

    public void addLineItem(OrderLineItem item) {
        item.assignTo(this);
        this.lineItems.add(item);
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getFailureReason() { return failureReason; }
    public List<OrderLineItem> getLineItems() { return lineItems; }
    public Instant getCreatedAt() { return createdAt; }
}