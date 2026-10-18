package com.aatzhmair.productservice.search;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public ProductSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<ProductDocument> search(String queryText) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .query(queryText)
                                .fields("name^2", "description")
                                .fuzziness("AUTO")
                        )
                )
                .build();

        return elasticsearchOperations.search(query, ProductDocument.class)
                .stream()
                .map(SearchHit::getContent)
                .toList();
    }
}
