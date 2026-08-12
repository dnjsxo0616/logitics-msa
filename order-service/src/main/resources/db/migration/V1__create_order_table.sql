CREATE TABLE order_schema.p_order
(
    id                  UUID PRIMARY KEY,
    receiver_company_id UUID        NOT NULL,
    supplier_company_id UUID        NOT NULL,
    product_id          UUID        NOT NULL,
    quantity            INTEGER     NOT NULL,
    status              VARCHAR(30) NOT NULL,
    request_message     TEXT        NOT NULL,

    created_at          TIMESTAMPTZ NOT NULL,
    created_by          UUID        NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,
    updated_by          UUID        NOT NULL,
    deleted_at          TIMESTAMPTZ,
    deleted_by          UUID,

    CONSTRAINT ck_order_quantity
        CHECK (quantity > 0),

    CONSTRAINT ck_order_status
        CHECK (status IN (
                          'PENDING',
                          'CONFIRMED',
                          'FAILED',
                          'CANCELED',
                          'COMPLETED'
            ))
);
