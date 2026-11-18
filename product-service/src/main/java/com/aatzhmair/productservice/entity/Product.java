package com.aatzhmair.productservice.entity;

import com.aatzhmair.productservice.exception.InsufficientStockException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products", schema = "product_schema")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    private String gender;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(name = "product_type")
    private String productType;

    private String colour;

    private String usage;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "image_url")
    private String imageUrl;

    @Version
    private long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Product() {}

    public Product(String name, String description, BigDecimal price, String category, int stockQuantity, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
    }

    public void applyUpdate(String name, String description, BigDecimal price, String category, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.updatedAt = Instant.now();
    }

    public void applyTaxonomy(String externalId, String gender, String subCategory, String productType, String colour, String usage) {
        this.externalId = externalId;
        this.gender = gender;
        this.subCategory = subCategory;
        this.productType = productType;
        this.colour = colour;
        this.usage = usage;
    }

    public void decreaseStock(int quantity) {
        if (quantity > this.stockQuantity) {
            throw new InsufficientStockException(id, quantity, stockQuantity);
        }
        this.stockQuantity -= quantity;
        this.updatedAt = Instant.now();
    }

    public void restoreStock(int quantity) {
        this.stockQuantity += quantity;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getExternalId() { return externalId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public String getGender() { return gender; }
    public String getSubCategory() { return subCategory; }
    public String getProductType() { return productType; }
    public String getColour() { return colour; }
    public String getUsage() { return usage; }
    public int getStockQuantity() { return stockQuantity; }
    public String getImageUrl() { return imageUrl; }
}