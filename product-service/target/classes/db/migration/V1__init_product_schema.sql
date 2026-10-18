CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE SCHEMA IF NOT EXISTS product_schema;

CREATE TABLE product_schema.products (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(255)  NOT NULL,
    description    TEXT,
    price          NUMERIC(10,2) NOT NULL,
    category       VARCHAR(100)  NOT NULL,
    stock_quantity INTEGER       NOT NULL DEFAULT 0,
    image_url      VARCHAR(500),
    version        BIGINT        NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_products_category ON product_schema.products (category);
