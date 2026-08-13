ALTER TABLE notification_schema.p_ai_alert
    DROP CONSTRAINT ck_ai_alert_status;

ALTER TABLE notification_schema.p_ai_alert
    ADD CONSTRAINT ck_ai_alert_status
        CHECK (status IN (
            'PENDING',
            'PROCESSING',
            'COMPLETED',
            'FAILED',
            'CANCELED'
        ));

ALTER TABLE notification_schema.p_ai_alert
    DROP CONSTRAINT ck_ai_alert_retry_count,
    DROP COLUMN retry_count,
    DROP COLUMN next_retry_at;
