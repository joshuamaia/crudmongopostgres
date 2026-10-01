package br.com.joshua.crudmongopostgres.shared.domain.exception;

/**
 * Base type for business rule violations raised by the domain and application layers.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
