package br.com.joshua.crudmongopostgres.product.adapter.out.persistence.postgres;

import br.com.joshua.crudmongopostgres.product.domain.model.ProductSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Small, composable JPA specifications for {@link ProductJpaEntity}.
 */
public final class ProductJpaSpecifications {

    private static final char LIKE_ESCAPE = '\\';

    private ProductJpaSpecifications() {
    }

    public static Specification<ProductJpaEntity> fromCriteria(ProductSearchCriteria criteria) {
        List<Specification<ProductJpaEntity>> specifications = new ArrayList<>();
        if (criteria.name() != null) {
            specifications.add(nameContains(criteria.name()));
        }
        if (criteria.category() != null) {
            specifications.add(categoryEquals(criteria.category()));
        }
        if (criteria.minPrice() != null) {
            specifications.add(priceGreaterThanOrEqualTo(criteria.minPrice()));
        }
        if (criteria.maxPrice() != null) {
            specifications.add(priceLessThanOrEqualTo(criteria.maxPrice()));
        }
        if (criteria.active() != null) {
            specifications.add(activeEquals(criteria.active()));
        }
        return Specification.allOf(specifications);
    }

    public static Specification<ProductJpaEntity> nameContains(String name) {
        String pattern = "%" + escapeLike(name.toLowerCase(Locale.ROOT)) + "%";
        return (root, query, builder) -> builder.like(builder.lower(root.get("name")), pattern, LIKE_ESCAPE);
    }

    public static Specification<ProductJpaEntity> categoryEquals(String category) {
        String normalized = category.toLowerCase(Locale.ROOT);
        return (root, query, builder) -> builder.equal(builder.lower(root.get("category")), normalized);
    }

    public static Specification<ProductJpaEntity> priceGreaterThanOrEqualTo(BigDecimal minPrice) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<ProductJpaEntity> priceLessThanOrEqualTo(BigDecimal maxPrice) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<ProductJpaEntity> activeEquals(boolean active) {
        return (root, query, builder) -> builder.equal(root.get("active"), active);
    }

    private static String escapeLike(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
