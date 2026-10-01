package br.com.joshua.crudmongopostgres.shared.domain.exception;

public class InvalidPageQueryException extends DomainException {

    public InvalidPageQueryException(String message) {
        super(message);
    }
}
