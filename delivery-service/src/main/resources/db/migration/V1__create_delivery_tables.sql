CREATE TABLE delivery_schema.p_delivery_manager
(
    id         UUID PRIMARY KEY,
    slack_id   VARCHAR(50)  NOT NULL,
    hub_id     UUID,
    type       VARCHAR(50)  NOT NULL,
    sequence   INTEGER      NOT NULL,
    last_assigned_at TIMESTAMPTZ,

    created_at TIMESTAMPTZ  NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    deleted_at TIMESTAMPTZ,
    deleted_by VARCHAR(100),

    CONSTRAINT ck_delivery_manager_type
        CHECK (type IN ('HUB', 'COMPANY')),

    CONSTRAINT ck_delivery_manager_hub_id
        CHECK (
            (type = 'COMPANY' AND hub_id IS NOT NULL)
                OR
            (type = 'HUB' AND hub_id IS NULL)
            ),

    CONSTRAINT ck_delivery_manager_sequence
        CHECK (sequence >= 1)
);

CREATE UNIQUE INDEX uq_company_hub_sequence
    ON delivery_schema.p_delivery_manager (hub_id, sequence) WHERE type = 'COMPANY';

CREATE UNIQUE INDEX uq_hub_sequence
    ON delivery_schema.p_delivery_manager (sequence) WHERE type = 'HUB';


CREATE TABLE delivery_schema.p_delivery
(
    id                          UUID PRIMARY KEY,
    order_id                    UUID         NOT NULL,
    departure_hub_id            UUID         NOT NULL,
    destination_hub_id          UUID         NOT NULL,
    company_delivery_manager_id UUID,

    status                      VARCHAR(50)  NOT NULL,

    delivery_address            VARCHAR(255) NOT NULL,
    recipient_name              VARCHAR(50)  NOT NULL,
    recipient_slack_id          VARCHAR(50)  NOT NULL,

    created_at                  TIMESTAMPTZ  NOT NULL,
    created_by                  VARCHAR(100) NOT NULL,
    updated_at                  TIMESTAMPTZ  NOT NULL,
    updated_by                  VARCHAR(100) NOT NULL,
    deleted_at                  TIMESTAMPTZ,
    deleted_by                  VARCHAR(100),

    CONSTRAINT uk_delivery_order_id
        UNIQUE (order_id),

    CONSTRAINT ck_delivery_status
        CHECK (status IN (
                          'WAITING_AT_DEPARTURE_HUB',
                          'MOVING_BETWEEN_HUBS',
                          'ARRIVED_AT_DESTINATION_HUB',
                          'MOVING_TO_COMPANY',
                          'DELIVERED',
                          'CANCELLED'
            )),

    CONSTRAINT fk_delivery_company_manager
        FOREIGN KEY (company_delivery_manager_id)
            REFERENCES delivery_schema.p_delivery_manager (id)
);


CREATE TABLE delivery_schema.p_delivery_route
(
    id                      UUID PRIMARY KEY,

    delivery_id             UUID           NOT NULL,

    hub_delivery_manager_id UUID,

    departure_hub_id        UUID           NOT NULL,
    arrival_hub_id          UUID           NOT NULL,

    sequence                INTEGER        NOT NULL,

    status                  VARCHAR(50)    NOT NULL,

    departed_at             TIMESTAMPTZ,
    arrived_at              TIMESTAMPTZ,

    expected_distance       NUMERIC(10, 2) NOT NULL,
    expected_duration       INTEGER        NOT NULL,

    actual_distance         NUMERIC(10, 2),
    actual_duration         INTEGER,

    created_at              TIMESTAMPTZ    NOT NULL,
    created_by              VARCHAR(100)   NOT NULL,
    updated_at              TIMESTAMPTZ    NOT NULL,
    updated_by              VARCHAR(100)   NOT NULL,
    deleted_at              TIMESTAMPTZ,
    deleted_by              VARCHAR(100),

    CONSTRAINT uk_delivery_route_sequence
        UNIQUE (delivery_id, sequence),

    CONSTRAINT ck_delivery_route_status
        CHECK (status IN (
                          'WAITING_FOR_DEPARTURE',
                          'IN_TRANSIT',
                          'ARRIVED'
            )),

    CONSTRAINT ck_delivery_route_sequence
        CHECK (sequence >= 1),

    CONSTRAINT ck_expected_distance
        CHECK (expected_distance >= 0),

    CONSTRAINT ck_expected_duration
        CHECK (expected_duration >= 0),

    CONSTRAINT ck_actual_distance
        CHECK (
            actual_distance IS NULL
                OR actual_distance >= 0
            ),

    CONSTRAINT ck_actual_duration
        CHECK (
            actual_duration IS NULL
                OR actual_duration >= 0
            ),

    CONSTRAINT ck_arrived_after_departed
        CHECK (
            departed_at IS NULL
                OR arrived_at IS NULL
                OR arrived_at >= departed_at
            ),

    CONSTRAINT fk_delivery_route_delivery
        FOREIGN KEY (delivery_id)
            REFERENCES delivery_schema.p_delivery (id),

    CONSTRAINT fk_delivery_route_manager
        FOREIGN KEY (hub_delivery_manager_id)
            REFERENCES delivery_schema.p_delivery_manager (id)
);