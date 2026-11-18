package com.aatzhmair.productservice.service;

import com.aatzhmair.common.events.ProductChangedEvent;
import com.aatzhmair.productservice.entity.Product;
import com.aatzhmair.productservice.repository.ProductRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProductImportService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ProductChangedEvent> kafkaTemplate;

    public ProductImportService(ProductRepository productRepository, KafkaTemplate<String, ProductChangedEvent> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public ImportResult importFrom(InputStream input) throws IOException {
        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).get().parse(reader)) {

            for (CSVRecord row : parser) {
                try {
                    String externalId = row.get("ProductId");
                    if (productRepository.existsByExternalId(externalId)) {
                        skipped++;
                        continue;
                    }

                    // The feed has no price or stock columns — these are demo placeholders standing in for
                    // what a real import would require as separate inputs from pricing/inventory systems.
                    BigDecimal placeholderPrice = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(999, 4999), 2);

                    Product product = new Product(
                            row.get("ProductTitle"),
                            "%s %s, %s, %s wear".formatted(row.get("ProductType"), row.get("SubCategory"), row.get("Colour"), row.get("Usage")),
                            placeholderPrice,
                            row.get("Category"),
                            100, // placeholder stock — same caveat as price
                            row.get("ImageURL")
                    );
                    product.applyTaxonomy(externalId, row.get("Gender"), row.get("SubCategory"),
                            row.get("ProductType"), row.get("Colour"), row.get("Usage"));

                    productRepository.save(product);
                    kafkaTemplate.send("product-events", product.getId().toString(), toEvent(product));
                    imported++;

                } catch (Exception e) {
                    errors.add("Row " + row.getRecordNumber() + ": " + e.getMessage());
                }
            }
        }

        return new ImportResult(imported, skipped, errors);
    }

    private ProductChangedEvent toEvent(Product product) {
        return new ProductChangedEvent(product.getId(), product.getName(), product.getDescription(),
                product.getCategory(), product.getPrice(), Instant.now());
    }

    public record ImportResult(int imported, int skipped, List<String> errors) {}
}