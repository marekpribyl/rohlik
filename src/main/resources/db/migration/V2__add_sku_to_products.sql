ALTER TABLE products ADD sku VARCHAR(50) NOT NULL ;

UPDATE products SET sku = CONCAT('LEGACY-', CAST(id AS VARCHAR)) WHERE sku IS NULL;

ALTER TABLE products ADD CONSTRAINT uk_products_sku UNIQUE (sku);

CREATE INDEX idx_products_sku ON products(sku);
