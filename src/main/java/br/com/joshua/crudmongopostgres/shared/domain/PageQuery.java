package br.com.joshua.crudmongopostgres.shared.domain;

import br.com.joshua.crudmongopostgres.shared.domain.exception.InvalidPageQueryException;

/**
 * Framework-agnostic pagination and sorting request.
 */
public record PageQuery(int page, int size, String sortBy, SortDirection direction) {

    public static final int MAX_PAGE_SIZE = 100;

    public PageQuery {
        if (page < 0) {
            throw new InvalidPageQueryException("Page index must not be negative");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidPageQueryException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (sortBy == null || sortBy.isBlank()) {
            throw new InvalidPageQueryException("Sort field must not be blank");
        }
        if (direction == null) {
            direction = SortDirection.ASC;
        }
    }
}
