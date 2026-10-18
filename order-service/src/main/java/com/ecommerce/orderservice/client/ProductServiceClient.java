package com.ecommerce.orderservice.client;

import com.ecommerce.orderservice.dto.ProductResponse;
import com.ecommerce.orderservice.dto.StockAdjustmentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "product-service", url = "${product-service.url}")
public interface ProductServiceClient {

    @GetMapping("/products/{id}")
    ProductResponse getById(@PathVariable UUID id);

    @PatchMapping("/products/{id}/stock")
    void decreaseStock(@PathVariable UUID id, @RequestBody StockAdjustmentRequest request);

    @PatchMapping("/products/{id}/stock/restore")
    void restoreStock(@PathVariable UUID id, @RequestBody StockAdjustmentRequest request);
}