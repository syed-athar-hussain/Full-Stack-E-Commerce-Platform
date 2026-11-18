ALTER TABLE product_schema.products
    ADD COLUMN external_id  VARCHAR(50) UNIQUE,
    ADD COLUMN gender       VARCHAR(30),
    ADD COLUMN sub_category VARCHAR(100),
    ADD COLUMN product_type VARCHAR(100),
    ADD COLUMN colour       VARCHAR(50),
    ADD COLUMN usage        VARCHAR(50);

CREATE INDEX idx_products_external_id ON product_schema.products (external_id);
CREATE INDEX idx_products_gender ON product_schema.products (gender);