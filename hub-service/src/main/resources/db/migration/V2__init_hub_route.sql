CREATE TABLE p_hub_route
(
    id               uuid PRIMARY KEY NOT NULL,
    departure_hub_id uuid             NOT NULL,
    arrival_hub_id   uuid             NOT NULL,
    duration_minutes int              NOT NULL,
    distance_km      numeric(10, 2)   NOT NULL,

    created_at       timestamptz      NOT NULL,
    created_by       uuid             NOT NULL,
    updated_at       timestamptz      NOT NULL,
    updated_by       uuid             NOT NULL,
    deleted_at       timestamptz,
    deleted_by       uuid,

    CONSTRAINT fk_route_departure
        FOREIGN KEY (departure_hub_id) REFERENCES p_hub (id),
    CONSTRAINT fk_route_arrival
        FOREIGN KEY (arrival_hub_id) REFERENCES p_hub (id),

    CONSTRAINT ck_route_duration
        CHECK (duration_minutes >= 0),
    CONSTRAINT ck_route_distance
        CHECK (distance_km >= 0)
);

CREATE UNIQUE INDEX uq_route_pair
    ON p_hub_route (departure_hub_id, arrival_hub_id)
    WHERE deleted_at IS NULL;

CREATE INDEX idx_route_arrival
    ON p_hub_route (arrival_hub_id);
