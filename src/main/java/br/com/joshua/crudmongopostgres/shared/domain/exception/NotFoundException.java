package br.com.joshua.crudmongopostgres.shared.domain.exception;

/**
 * Raised when a requested aggregate does not exist.
 */
public abstract class NotFoundException extends DomainException {

    protected NotFoundException(String message) {
        super(message);
    }
}
