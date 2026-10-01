package br.com.joshua.crudmongopostgres.product.application.port.in;

import br.com.joshua.crudmongopostgres.product.domain.model.Product;

import java.math.BigDecimal;

public interface CreateProductUseCase {

    Product execute(Command command);

    record Command(String name,
                   String description,
                   BigDecimal price,
                   int quantity,
                   String category,
                   boolean active) {
    }
}
