CREATE TABLE p_hub
(
    id         uuid PRIMARY KEY NOT NULL,
    name       varchar(100)     NOT NULL,
    address    varchar(255)     NOT NULL,
    latitude   numeric(9, 6)    NOT NULL,
    longitude  numeric(9, 6)    NOT NULL,

    created_at timestamptz      NOT NULL,
    created_by uuid             NOT NULL,
    updated_at timestamptz      NOT NULL,
    updated_by uuid             NOT NULL,
    deleted_at timestamptz,
    deleted_by uuid
);

CREATE UNIQUE INDEX uq_hub_name
    ON p_hub(name) WHERE deleted_at IS NULL;
