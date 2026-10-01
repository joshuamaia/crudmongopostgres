package br.com.joshua.crudmongopostgres.product.application.usecase;

import br.com.joshua.crudmongopostgres.product.application.port.in.GetProductByIdUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.out.ProductRepositoryPort;
import br.com.joshua.crudmongopostgres.product.domain.exception.ProductNotFoundException;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;

import java.util.UUID;

public class GetProductByIdService implements GetProductByIdUseCase {

    private final ProductRepositoryPort productRepository;

    public GetProductByIdService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
