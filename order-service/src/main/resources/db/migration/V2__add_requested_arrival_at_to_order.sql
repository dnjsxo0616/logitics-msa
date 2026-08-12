ALTER TABLE order_schema.p_order
    ADD COLUMN requested_arrival_at TIMESTAMPTZ NOT NULL,
    ALTER COLUMN request_message DROP NOT NULL;
