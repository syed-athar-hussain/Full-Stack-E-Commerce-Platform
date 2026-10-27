package com.aatzhmair.orderservice.exception;

import java.util.UUID;

public class ProductServiceUnavailableException extends RuntimeException {
    public ProductServiceUnavailableException(UUID productId, Throwable cause) {
        super("Product Service unavailable while handling product " + productId, cause);
    }
}