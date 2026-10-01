package br.com.joshua.crudmongopostgres.product.adapter.out.persistence.mongo;

import br.com.joshua.crudmongopostgres.product.domain.model.Product;

import java.util.UUID;

final class ProductDocumentMapper {

    private ProductDocumentMapper() {
    }

    static ProductDocument toDocument(Product product) {
        return new ProductDocument(
                product.getId().toString(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

    static Product toDomain(ProductDocument document) {
        return Product.restore(
                UUID.fromString(document.getId()),
                document.getName(),
                document.getDescription(),
                document.getPrice(),
                document.getQuantity(),
                document.getCategory(),
                document.isActive(),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }
}
