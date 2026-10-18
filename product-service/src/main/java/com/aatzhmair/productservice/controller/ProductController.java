package com.aatzhmair.productservice.controller;

import com.aatzhmair.productservice.dto.ProductRequest;
import com.aatzhmair.productservice.dto.ProductResponse;
import com.aatzhmair.productservice.dto.StockAdjustmentRequest;
import com.aatzhmair.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable UUID id) {
        return productService.getById(id);
    }

    @GetMapping("/search")
    public List<ProductResponse> search(@RequestParam String q) {
        return productService.search(q);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> decreaseStock(@PathVariable UUID id, @RequestBody StockAdjustmentRequest request) {
        productService.decreaseStock(id, request.quantity());
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/stock/restore")
    public ResponseEntity<Void> restoreStock(@PathVariable UUID id, @RequestBody StockAdjustmentRequest request) {
        productService.restoreStock(id, request.quantity());
        return ResponseEntity.noContent().build();
    }
}
