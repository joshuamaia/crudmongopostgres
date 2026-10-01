CREATE TABLE products
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(120)   NOT NULL,
    description VARCHAR(500),
    price       NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
    quantity    INTEGER        NOT NULL CHECK (quantity >= 0),
    category    VARCHAR(60)    NOT NULL,
    active      BOOLEAN        NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_products_category ON products (LOWER(category));
CREATE INDEX idx_products_price ON products (price);
