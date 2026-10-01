package br.com.joshua.crudmongopostgres.product.adapter.out.persistence.mongo;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

/**
 * Specification pattern for MongoDB: each specification produces a {@link Criteria}
 * and specifications can be composed with {@link #and(MongoSpecification)} / {@link #allOf(List)}.
 *
 * @param <T> document type the specification applies to
 */
@FunctionalInterface
public interface MongoSpecification<T> {

    Criteria toCriteria();

    default MongoSpecification<T> and(MongoSpecification<T> other) {
        return () -> new Criteria().andOperator(toCriteria(), other.toCriteria());
    }

    static <T> MongoSpecification<T> unrestricted() {
        return Criteria::new;
    }

    static <T> MongoSpecification<T> allOf(List<MongoSpecification<T>> specifications) {
        if (specifications.isEmpty()) {
            return unrestricted();
        }
        if (specifications.size() == 1) {
            return specifications.getFirst();
        }
        return () -> new Criteria().andOperator(specifications.stream().map(MongoSpecification::toCriteria).toList());
    }
}
