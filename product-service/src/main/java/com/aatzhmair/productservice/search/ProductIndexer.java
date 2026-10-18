// product-service — search/ProductIndexer.java (new file)
package com.aatzhmair.productservice.search;

import com.aatzhmair.common.events.ProductChangedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductIndexer {

    private final ProductSearchRepository productSearchRepository;

    public ProductIndexer(ProductSearchRepository productSearchRepository) {
        this.productSearchRepository = productSearchRepository;
    }

    @KafkaListener(topics = "product-events", groupId = "product-service-indexer")
    public void onProductChanged(ProductChangedEvent event) {
        productSearchRepository.save(new ProductDocument(
                event.productId().toString(), event.name(), event.description(), event.category(), event.price()));
    }
}