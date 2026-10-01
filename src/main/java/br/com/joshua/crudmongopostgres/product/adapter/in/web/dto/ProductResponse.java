package br.com.joshua.crudmongopostgres.product.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        int quantity,
        String category,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {
}
