package br.com.joshua.crudmongopostgres.product.application.port.in;

import br.com.joshua.crudmongopostgres.product.domain.model.Product;

import java.util.UUID;

public interface GetProductByIdUseCase {

    Product execute(UUID id);
}
