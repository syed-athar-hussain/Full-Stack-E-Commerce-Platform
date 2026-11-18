package com.aatzhmair.orderservice.client;

import com.aatzhmair.orderservice.dto.ProductResponse;
import com.aatzhmair.orderservice.dto.StockAdjustmentRequest;
import com.aatzhmair.orderservice.exception.ProductServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductServiceGateway {

    private final ProductServiceClient productServiceClient;

    public ProductServiceGateway(ProductServiceClient productServiceClient) {
        this.productServiceClient = productServiceClient;
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "getByIdFallback")
    @Retry(name = "productService")
    public ProductResponse getById(UUID id) {
        return productServiceClient.getById(id);
    }
    @CircuitBreaker(name = "productService", fallbackMethod = "mutationFallback")
    public void decreaseStock(UUID id, StockAdjustmentRequest request) {
        productServiceClient.decreaseStock(id, request);
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "mutationFallback")
    public void restoreStock(UUID id, StockAdjustmentRequest request) {
        productServiceClient.restoreStock(id, request);
    }

    private ProductResponse getByIdFallback(UUID id, Throwable t) {
        throw new ProductServiceUnavailableException(id, t);
    }

    private void mutationFallback(UUID id, StockAdjustmentRequest request, Throwable t) {
        throw new ProductServiceUnavailableException(id, t);
    }
}