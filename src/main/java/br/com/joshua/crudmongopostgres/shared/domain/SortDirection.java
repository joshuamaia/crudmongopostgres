package br.com.joshua.crudmongopostgres.shared.domain;

import br.com.joshua.crudmongopostgres.shared.domain.exception.InvalidPageQueryException;

import java.util.Locale;

public enum SortDirection {
    ASC,
    DESC;

    public static SortDirection from(String value) {
        if (value == null || value.isBlank()) {
            return ASC;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new InvalidPageQueryException("Invalid sort direction '%s'. Allowed values: ASC, DESC".formatted(value));
        }
    }
}
