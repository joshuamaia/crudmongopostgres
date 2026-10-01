package br.com.joshua.crudmongopostgres.product.application.port.in;

import br.com.joshua.crudmongopostgres.product.domain.model.Product;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateProductUseCase {

    Product execute(Command command);

    record Command(UUID id,
                   String name,
                   String description,
                   BigDecimal price,
                   int quantity,
                   String category,
                   boolean active) {
    }
}
