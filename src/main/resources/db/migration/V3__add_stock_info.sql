ALTER TABLE products ALTER stock_quantity SET DEFAULT 0;
ALTER TABLE products ADD reserved_quantity NUMERIC NOT NULL DEFAULT 0;
