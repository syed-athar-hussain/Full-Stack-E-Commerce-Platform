package com.aatzhmair.orderservice.exception;

import java.util.UUID;

public class OrderFailedException extends RuntimeException {
    public OrderFailedException(UUID orderId, String reason, Throwable cause) {
        super("Order " + orderId + " failed: " + reason, cause);
    }
}