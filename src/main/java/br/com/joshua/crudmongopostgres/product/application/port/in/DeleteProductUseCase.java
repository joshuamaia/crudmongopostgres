package br.com.joshua.crudmongopostgres.product.application.port.in;

import java.util.UUID;

public interface DeleteProductUseCase {

    void execute(UUID id);
}
