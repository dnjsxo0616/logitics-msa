CREATE TABLE user_schema.p_user (
        id UUID NOT NULL,
        created_at TIMESTAMPTZ(6) NOT NULL,
        created_by UUID NOT NULL,
        deleted_at TIMESTAMPTZ(6),
        deleted_by UUID,
        updated_at TIMESTAMPTZ(6) NOT NULL,
        updated_by UUID NOT NULL,

        company_id UUID,
        email VARCHAR(255) NOT NULL,
        hub_id UUID,

        nickname VARCHAR(20) NOT NULL,
        password VARCHAR(255) NOT NULL,
        role VARCHAR(255) NOT NULL,
        slack_id VARCHAR(255) NOT NULL,
        status VARCHAR(255) NOT NULL,
        username VARCHAR(50) NOT NULL,

        CONSTRAINT p_user_pkey
            PRIMARY KEY (id),

        CONSTRAINT uk_user_nickname
            UNIQUE (nickname),

        CONSTRAINT uk_user_email
            UNIQUE (email),

        CONSTRAINT uk_user_username
            UNIQUE (username),

        CONSTRAINT p_user_role_check
            CHECK (
                role IN (
                         'MASTER',
                         'HUB_MANAGER',
                         'DELIVERY_MANAGER',
                         'SUPPLIER_MANAGER'
                    )
                ),

        CONSTRAINT p_user_status_check
            CHECK (
                status IN (
                           'PENDING',
                           'APPROVED',
                           'REJECTED',
                           'DELETED'
                    )
                )
);

-- MASTER 계정 초기 데이터
-- ID는 고정하여 DB 초기화 시에도 동일한 MASTER 계정을 생성한다.

CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO user_schema.p_user (
    id,
    created_at,
    created_by,
    deleted_at,
    deleted_by,
    updated_at,
    updated_by,
    company_id,
    email,
    hub_id,
    nickname,
    "password",
    "role",
    slack_id,
    status,
    username
)
VALUES (
           '00000000-0000-0000-0000-000000000001',
           NOW(),
           '00000000-0000-0000-0000-000000000001',
           NULL,
           NULL,
           NOW(),
           '00000000-0000-0000-0000-000000000001',
           NULL,
           'master@logistics.com',
           NULL,
           'MASTER',
           crypt('admin1234', gen_salt('bf', 10)),
           'MASTER',
           'master',
           'APPROVED',
           'master'
       )
    ON CONFLICT (username) DO NOTHING;