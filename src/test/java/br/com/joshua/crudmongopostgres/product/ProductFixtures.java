package br.com.joshua.crudmongopostgres.product;

import br.com.joshua.crudmongopostgres.product.domain.model.Product;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

public final class ProductFixtures {

    public static final Instant CREATED_AT = Instant.parse("2026-01-10T10:00:00Z");
    public static final Instant NOW = Instant.parse("2026-01-15T12:30:00Z");
    public static final UUID PRODUCT_ID = UUID.fromString("7b0c2f3e-6f51-4b5a-9a4e-2f1d3c9e8a10");

    private ProductFixtures() {
    }

    public static Clock fixedClock() {
        return Clock.fixed(NOW, ZoneOffset.UTC);
    }

    public static Product aProduct() {
        return Product.restore(
                PRODUCT_ID,
                "Mechanical Keyboard",
                "RGB mechanical keyboard with brown switches",
                new BigDecimal("499.90"),
                25,
                "Peripherals",
                true,
                CREATED_AT,
                CREATED_AT);
    }
}
