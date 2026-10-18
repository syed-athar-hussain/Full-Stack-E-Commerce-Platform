CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE SCHEMA IF NOT EXISTS order_schema;

CREATE TABLE order_schema.orders (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID NOT NULL,
    idempotency_key   VARCHAR(100) NOT NULL UNIQUE,
    status            VARCHAR(20) NOT NULL,
    total_amount      NUMERIC(10,2),
    payment_intent_id VARCHAR(255),
    failure_reason    TEXT,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE order_schema.order_line_items (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id    UUID NOT NULL REFERENCES order_schema.orders(id) ON DELETE CASCADE,
    product_id  UUID NOT NULL,
    quantity    INTEGER NOT NULL,
    unit_price  NUMERIC(10,2) NOT NULL
);

CREATE INDEX idx_orders_user_id ON order_schema.orders (user_id);
CREATE INDEX idx_order_line_items_order_id ON order_schema.order_line_items (order_id);