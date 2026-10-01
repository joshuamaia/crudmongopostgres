package br.com.joshua.crudmongopostgres.product.domain.model;

import br.com.joshua.crudmongopostgres.product.domain.exception.InvalidProductException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Product aggregate root. Persistence-agnostic and responsible for its own invariants.
 */
public class Product {

    public static final int NAME_MAX_LENGTH = 120;
    public static final int DESCRIPTION_MAX_LENGTH = 500;
    public static final int CATEGORY_MAX_LENGTH = 60;
    public static final int PRICE_SCALE = 2;

    private final UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private int quantity;
    private String category;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    private Product(UUID id, String name, String description, BigDecimal price, int quantity,
                    String category, boolean active, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        applyChanges(name, description, price, quantity, category, active);
    }

    public static Product create(String name, String description, BigDecimal price, int quantity,
                                 String category, boolean active, Instant now) {
        return new Product(UUID.randomUUID(), name, description, price, quantity, category, active, now, now);
    }

    public static Product restore(UUID id, String name, String description, BigDecimal price, int quantity,
                                  String category, boolean active, Instant createdAt, Instant updatedAt) {
        return new Product(id, name, description, price, quantity, category, active, createdAt, updatedAt);
    }

    public void update(String name, String description, BigDecimal price, int quantity,
                       String category, boolean active, Instant now) {
        applyChanges(name, description, price, quantity, category, active);
        this.updatedAt = Objects.requireNonNull(now, "now must not be null");
    }

    private void applyChanges(String name, String description, BigDecimal price, int quantity,
                              String category, boolean active) {
        this.name = requireText(name, "name", NAME_MAX_LENGTH);
        this.description = optionalText(description, "description", DESCRIPTION_MAX_LENGTH);
        this.price = requireValidPrice(price);
        this.quantity = requireValidQuantity(quantity);
        this.category = requireText(category, "category", CATEGORY_MAX_LENGTH);
        this.active = active;
    }

    private static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new InvalidProductException("Product %s must not be blank".formatted(field));
        }
        return checkLength(value.trim(), field, maxLength);
    }

    private static String optionalText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return checkLength(value.trim(), field, maxLength);
    }

    private static String checkLength(String value, String field, int maxLength) {
        if (value.length() > maxLength) {
            throw new InvalidProductException("Product %s must have at most %d characters".formatted(field, maxLength));
        }
        return value;
    }

    private static BigDecimal requireValidPrice(BigDecimal price) {
        if (price == null) {
            throw new InvalidProductException("Product price must not be null");
        }
        if (price.signum() < 0) {
            throw new InvalidProductException("Product price must not be negative");
        }
        return price.setScale(PRICE_SCALE, RoundingMode.HALF_EVEN);
    }

    private static int requireValidQuantity(int quantity) {
        if (quantity < 0) {
            throw new InvalidProductException("Product quantity must not be negative");
        }
        return quantity;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCategory() {
        return category;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof Product product && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
