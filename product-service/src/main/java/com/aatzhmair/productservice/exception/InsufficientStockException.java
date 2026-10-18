package com.aatzhmair.productservice.exception;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(UUID productId, int requested, int available) {
        super("Product " + productId + " has " + available + " in stock, requested " + requested);
    }
}
