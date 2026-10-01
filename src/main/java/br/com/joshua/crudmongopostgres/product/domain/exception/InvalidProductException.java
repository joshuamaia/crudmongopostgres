package br.com.joshua.crudmongopostgres.product.domain.exception;

import br.com.joshua.crudmongopostgres.shared.domain.exception.DomainException;

public class InvalidProductException extends DomainException {

    public InvalidProductException(String message) {
        super(message);
    }
}
