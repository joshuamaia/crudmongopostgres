package br.com.joshua.crudmongopostgres.product.domain.exception;

import br.com.joshua.crudmongopostgres.shared.domain.exception.NotFoundException;

import java.util.UUID;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(UUID id) {
        super("Product with id '%s' was not found".formatted(id));
    }
}
