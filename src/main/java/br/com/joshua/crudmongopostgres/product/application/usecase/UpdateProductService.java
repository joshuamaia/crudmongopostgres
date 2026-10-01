package br.com.joshua.crudmongopostgres.product.application.usecase;

import br.com.joshua.crudmongopostgres.product.application.port.in.UpdateProductUseCase;
import br.com.joshua.crudmongopostgres.product.application.port.out.ProductRepositoryPort;
import br.com.joshua.crudmongopostgres.product.domain.exception.ProductNotFoundException;
import br.com.joshua.crudmongopostgres.product.domain.model.Product;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class UpdateProductService implements UpdateProductUseCase {

    private final ProductRepositoryPort productRepository;
    private final Clock clock;

    public UpdateProductService(ProductRepositoryPort productRepository, Clock clock) {
        this.productRepository = productRepository;
        this.clock = clock;
    }

    @Override
    public Product execute(Command command) {
        Product product = productRepository.findById(command.id())
                .orElseThrow(() -> new ProductNotFoundException(command.id()));
        product.update(
                command.name(),
                command.description(),
                command.price(),
                command.quantity(),
                command.category(),
                command.active(),
                Instant.now(clock).truncatedTo(ChronoUnit.MILLIS));
        return productRepository.save(product);
    }
}
