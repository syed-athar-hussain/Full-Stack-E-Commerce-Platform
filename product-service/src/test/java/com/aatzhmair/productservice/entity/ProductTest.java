package com.aatzhmair.productservice.entity;

import com.aatzhmair.productservice.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void decreaseStock_reducesQuantityWhenSufficient() {
        Product product = new Product("Keyboard", "Mechanical", BigDecimal.valueOf(89.99), "Electronics", 10, null);
        product.decreaseStock(3);
        assertThat(product.getStockQuantity()).isEqualTo(7);
    }

    @Test
    void decreaseStock_throwsWhenInsufficientStock() {
        Product product = new Product("Keyboard", "Mechanical", BigDecimal.valueOf(89.99), "Electronics", 2, null);
        assertThatThrownBy(() -> product.decreaseStock(5)).isInstanceOf(InsufficientStockException.class);
        assertThat(product.getStockQuantity()).isEqualTo(2); // unchanged on failure
    }
}
