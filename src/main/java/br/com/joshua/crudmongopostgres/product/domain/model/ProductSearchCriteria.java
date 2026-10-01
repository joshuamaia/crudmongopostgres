package br.com.joshua.crudmongopostgres.product.domain.model;

import java.math.BigDecimal;

/**
 * Optional filters used to search products. Every non-null attribute becomes a specification combined with AND.
 */
public record ProductSearchCriteria(String name,
                                    String category,
                                    BigDecimal minPrice,
                                    BigDecimal maxPrice,
                                    Boolean active) {

    public ProductSearchCriteria {
        name = blankToNull(name);
        category = blankToNull(category);
    }

    public static ProductSearchCriteria empty() {
        return new ProductSearchCriteria(null, null, null, null, null);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
