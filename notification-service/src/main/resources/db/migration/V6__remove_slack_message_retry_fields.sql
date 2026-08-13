ALTER TABLE notification_schema.p_slack_message
    DROP CONSTRAINT ck_slack_message_status;

ALTER TABLE notification_schema.p_slack_message
    ADD CONSTRAINT ck_slack_message_status
        CHECK (status IN (
            'WAITING_CONFIRMATION',
            'SENDING',
            'SENT',
            'FAILED',
            'CANCELED'
        ));

ALTER TABLE notification_schema.p_slack_message
    DROP CONSTRAINT ck_slack_message_retry_count,
    DROP COLUMN retry_count,
    DROP COLUMN next_retry_at;
