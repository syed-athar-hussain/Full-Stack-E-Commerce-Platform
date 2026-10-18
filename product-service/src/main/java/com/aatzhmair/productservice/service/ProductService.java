package com.aatzhmair.productservice.service;

import com.aatzhmair.productservice.dto.ProductRequest;
import com.aatzhmair.productservice.dto.ProductResponse;
import com.aatzhmair.productservice.entity.Product;
import com.aatzhmair.productservice.exception.ProductNotFoundException;
import com.aatzhmair.productservice.repository.ProductRepository;
import com.aatzhmair.productservice.search.ProductDocument;
import com.aatzhmair.productservice.search.ProductSearchRepository;
import com.aatzhmair.productservice.search.ProductSearchService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ProductChangedEvent> kafkaTemplate;
    private final ProductSearchService productSearchService;

    public ProductService(ProductRepository productRepository,
                          ProductSearchRepository productSearchRepository,
                          ProductSearchService productSearchService) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
        this.productSearchService = productSearchService;
    }

    public ProductResponse create(ProductRequest request) {
        Product product = new Product(request.name(), request.description(), request.price(),
                request.category(), request.stockQuantity(), request.imageUrl());
        productRepository.save(product);
        indexInSearch(product);
        return toResponse(product);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponse getById(UUID id) {
        return productRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @CacheEvict(value = "products", key = "#id")
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.applyUpdate(request.name(), request.description(), request.price(), request.category(), request.imageUrl());
        productRepository.save(product);
        indexInSearch(product);
        return toResponse(product);
    }

    @CacheEvict(value = "products", key = "#id")
    public void decreaseStock(UUID id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.decreaseStock(quantity);
        productRepository.save(product); // @Version bumps here; a losing concurrent writer gets a 409, not a silent oversell
    }
    @CacheEvict(value = "products", key = "#id")
    public void restoreStock(UUID id, int quantity) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.restoreStock(quantity);
        productRepository.save(product);
    }

    public List<ProductResponse> search(String query) {
        return productSearchService.search(query).stream()
                .map(doc -> UUID.fromString(doc.getId()))
                .flatMap(id -> {
                    try {
                        return Stream.of(getById(id));
                    } catch (ProductNotFoundException e) {
                        return Stream.empty(); // ES and Postgres drifted — drop it rather than fail the whole search
                    }
                })
                .toList();
    }

    /*private void indexInSearch(Product product) {
        productSearchRepository.save(new ProductDocument(product.getId().toString(), product.getName(),
                product.getDescription(), product.getCategory(), product.getPrice()));
    }*/

    private void publishChange(Product product) {
        kafkaTemplate.send("product-events", product.getId().toString(),
                new ProductChangedEvent(product.getId(), product.getName(), product.getDescription(),
                        product.getCategory(), product.getPrice(), Instant.now()));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getCategory(), product.getStockQuantity(), product.getImageUrl());
    }
}
