package br.com.joshua.crudmongopostgres.product.adapter.in.web;

import br.com.joshua.crudmongopostgres.product.adapter.in.web.dto.ProductRequest;
import br.com.joshua.crudmongopostgres.product.adapter.in.web.dto.ProductResponse;
import br.com.joshua.crudmongopostgres.product.application.port.in.CreateProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.in.UpdateProductUseCase;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;

import java.util.UUID;

final class ProductWebMapper {

    private ProductWebMapper() {
    }

    static CreateProductUseCase.Command toCreateCommand(ProductRequest request) {
        return new CreateProductUseCase.Command(
                request.name(),
                request.description(),
                request.price(),
                request.quantity(),
                request.category(),
                request.active());
    }

    static UpdateProductUseCase.Command toUpdateCommand(UUID id, ProductRequest request) {
        return new UpdateProductUseCase.Command(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.quantity(),
                request.category(),
                request.active());
    }

    static ProductResponse toResponse(Product product) {
        return new ProductResponse(
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
}
