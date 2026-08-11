CREATE TABLE notification_schema.p_slack_message
(
    id                UUID PRIMARY KEY,
    ai_alert_id       UUID         NOT NULL,
    slack_receiver_id VARCHAR(100) NOT NULL,
    message           TEXT         NOT NULL,
    status            VARCHAR(30)  NOT NULL DEFAULT 'WAITING_CONFIRMATION',
    sent_at           TIMESTAMPTZ,
    error_message     TEXT,
    retry_count       INTEGER      NOT NULL DEFAULT 0,
    next_retry_at     TIMESTAMPTZ,

    created_at        TIMESTAMPTZ  NOT NULL,
    created_by        VARCHAR(100) NOT NULL,
    updated_at        TIMESTAMPTZ  NOT NULL,
    updated_by        VARCHAR(100) NOT NULL,
    deleted_at        TIMESTAMPTZ,
    deleted_by        VARCHAR(100),

    CONSTRAINT uk_slack_message_ai_alert_id
        UNIQUE (ai_alert_id),

    CONSTRAINT fk_slack_message_ai_alert
        FOREIGN KEY (ai_alert_id)
            REFERENCES notification_schema.p_ai_alert (id),

    CONSTRAINT ck_slack_message_status
        CHECK (status IN (
                          'WAITING_CONFIRMATION',
                          'SENDING',
                          'SENT',
                          'RETRY_WAIT',
                          'FAILED',
                          'CANCELED'
            )),

    CONSTRAINT ck_slack_message_retry_count
        CHECK (retry_count >= 0)
);
