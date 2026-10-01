package br.com.joshua.crudmongopostgres.product.adapter.out.persistence.postgres;

import br.com.joshua.crudmongopostgres.product.domain.model.Product;

final class ProductJpaMapper {

    private ProductJpaMapper() {
    }

    static ProductJpaEntity toEntity(Product product) {
        return new ProductJpaEntity(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

    static Product toDomain(ProductJpaEntity entity) {
        return Product.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getQuantity(),
                entity.getCategory(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
