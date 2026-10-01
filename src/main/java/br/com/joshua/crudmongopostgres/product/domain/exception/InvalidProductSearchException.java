package br.com.joshua.crudmongopostgres.product.domain.exception;

import br.com.joshua.crudmongopostgres.shared.domain.exception.DomainException;

public class InvalidProductSearchException extends DomainException {

    public InvalidProductSearchException(String message) {
        super(message);
    }
}
