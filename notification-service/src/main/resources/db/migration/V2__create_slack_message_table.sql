CREATE TABLE notification_schema.p_slack_message
(
    id                UUID         PRIMARY KEY,
    ai_alert_id       UUID,
    slack_receiver_id VARCHAR(255) NOT NULL,
    message           TEXT         NOT NULL,
    sent_at           TIMESTAMPTZ  NOT NULL,

    created_at        TIMESTAMPTZ  NOT NULL,
    created_by        VARCHAR(100) NOT NULL,
    updated_at        TIMESTAMPTZ  NOT NULL,
    updated_by        VARCHAR(100) NOT NULL,
    deleted_at        TIMESTAMPTZ,
    deleted_by        VARCHAR(100),

    CONSTRAINT fk_slack_message_ai_alert
        FOREIGN KEY (ai_alert_id)
            REFERENCES notification_schema.p_ai_alert (id)
);

CREATE UNIQUE INDEX uq_slack_message_ai_alert_active
    ON notification_schema.p_slack_message (ai_alert_id)
    WHERE ai_alert_id IS NOT NULL
        AND deleted_at IS NULL;
