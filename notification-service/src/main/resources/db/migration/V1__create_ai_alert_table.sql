CREATE TABLE notification_schema.p_ai_alert
(
    id              UUID        PRIMARY KEY,
    order_id        UUID        NOT NULL,
    delivery_id     UUID        NOT NULL,
    request_payload JSONB       NOT NULL,

    status          VARCHAR(30) NOT NULL,
    failure_stage   VARCHAR(30),
    failure_message TEXT,
    failed_at       TIMESTAMPTZ,

    prompt          TEXT,
    ai_response     TEXT,
    final_deadline  TIMESTAMPTZ,

    created_at      TIMESTAMPTZ NOT NULL,
    created_by      VARCHAR(100) NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    updated_by      VARCHAR(100) NOT NULL,
    deleted_at      TIMESTAMPTZ,
    deleted_by      VARCHAR(100),

    CONSTRAINT uk_ai_alert_delivery_id
        UNIQUE (delivery_id),

    CONSTRAINT ck_ai_alert_status
        CHECK (status IN (
                          'RECEIVED',
                          'AI_COMPLETED',
                          'SENT'
            )),

    CONSTRAINT ck_ai_alert_failure_stage
        CHECK (
            failure_stage IS NULL
                OR failure_stage IN (
                                     'AI_CALL',
                                     'AI_RESPONSE_PARSE',
                                     'AI_VALIDATION',
                                     'SLACK_SEND'
                )
            )
);
