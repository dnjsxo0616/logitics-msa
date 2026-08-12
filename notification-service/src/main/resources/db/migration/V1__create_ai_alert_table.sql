CREATE TABLE notification_schema.p_ai_alert
(
    id               UUID PRIMARY KEY,
    order_id         UUID         NOT NULL,
    delivery_id      UUID         NOT NULL,
    delivery_payload JSONB        NOT NULL,
    prompt           TEXT,
    ai_response      TEXT,
    final_deadline   TIMESTAMPTZ,
    status           VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    error_message    TEXT,
    retry_count      INTEGER      NOT NULL DEFAULT 0,
    next_retry_at    TIMESTAMPTZ,

    created_at       TIMESTAMPTZ  NOT NULL,
    created_by       VARCHAR(100) NOT NULL,
    updated_at       TIMESTAMPTZ  NOT NULL,
    updated_by       VARCHAR(100) NOT NULL,
    deleted_at       TIMESTAMPTZ,
    deleted_by       VARCHAR(100),

    CONSTRAINT uk_ai_alert_delivery_id
        UNIQUE (delivery_id),

    CONSTRAINT ck_ai_alert_status
        CHECK (status IN (
                          'PENDING',
                          'PROCESSING',
                          'COMPLETED',
                          'RETRY_WAIT',
                          'FAILED',
                          'CANCELED'
            )),

    CONSTRAINT ck_ai_alert_retry_count
        CHECK (retry_count >= 0)
);
