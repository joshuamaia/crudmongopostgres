package br.com.joshua.crudmongopostgres.product.domain.model;

import java.util.Arrays;
import java.util.Optional;

/**
 * Product attributes that may be used for sorting. The property names are shared by every persistence adapter.
 */
public enum ProductSortField {
    NAME("name"),
    PRICE("price"),
    QUANTITY("quantity"),
    CATEGORY("category"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String property;

    ProductSortField(String property) {
        this.property = property;
    }

    public String property() {
        return property;
    }

    public static Optional<ProductSortField> fromProperty(String property) {
        return Arrays.stream(values())
                .filter(field -> field.property.equalsIgnoreCase(property))
                .findFirst();
    }
}
