package br.com.joshua.crudmongopostgres.product.adapter.out.persistence.mongo;

import br.com.joshua.crudmongopostgres.product.domain.model.ProductSearchCriteria;
import org.bson.types.Decimal128;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Small, composable MongoDB specifications for {@link ProductDocument}.
 */
public final class ProductMongoSpecifications {

    private ProductMongoSpecifications() {
    }

    public static MongoSpecification<ProductDocument> fromCriteria(ProductSearchCriteria criteria) {
        List<MongoSpecification<ProductDocument>> specifications = new ArrayList<>();
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
        return MongoSpecification.allOf(specifications);
    }

    public static MongoSpecification<ProductDocument> nameContains(String name) {
        Pattern pattern = Pattern.compile(Pattern.quote(name), Pattern.CASE_INSENSITIVE);
        return () -> Criteria.where("name").regex(pattern);
    }

    public static MongoSpecification<ProductDocument> categoryEquals(String category) {
        Pattern pattern = Pattern.compile("^" + Pattern.quote(category) + "$", Pattern.CASE_INSENSITIVE);
        return () -> Criteria.where("category").regex(pattern);
    }

    public static MongoSpecification<ProductDocument> priceGreaterThanOrEqualTo(BigDecimal minPrice) {
        return () -> Criteria.where("price").gte(new Decimal128(minPrice));
    }

    public static MongoSpecification<ProductDocument> priceLessThanOrEqualTo(BigDecimal maxPrice) {
        return () -> Criteria.where("price").lte(new Decimal128(maxPrice));
    }

    public static MongoSpecification<ProductDocument> activeEquals(boolean active) {
        return () -> Criteria.where("active").is(active);
    }
}
