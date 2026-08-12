ALTER TABLE notification_schema.p_ai_alert
    ADD COLUMN request_payload JSONB;

UPDATE notification_schema.p_ai_alert
SET request_payload = (COALESCE(order_payload, '{}'::jsonb)
        - 'status'
        - 'requesterUserId'
        - 'receiverName'
        - 'receiverSlackId')
        || delivery_payload;

ALTER TABLE notification_schema.p_ai_alert
    ALTER COLUMN request_payload SET NOT NULL,
    DROP COLUMN order_payload,
    DROP COLUMN delivery_payload;
