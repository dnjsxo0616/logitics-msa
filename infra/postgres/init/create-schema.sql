-- 서비스별 Schema 생성

CREATE SCHEMA IF NOT EXISTS user_schema;

CREATE SCHEMA IF NOT EXISTS company_schema;

CREATE SCHEMA IF NOT EXISTS product_schema;

CREATE SCHEMA IF NOT EXISTS order_schema;

CREATE SCHEMA IF NOT EXISTS delivery_schema;

CREATE SCHEMA IF NOT EXISTS hub_schema;

CREATE SCHEMA IF NOT EXISTS notification_schema;


-- 권한 부여
GRANT ALL PRIVILEGES ON SCHEMA user_schema TO iftrue;
GRANT ALL PRIVILEGES ON SCHEMA company_schema TO iftrue;
GRANT ALL PRIVILEGES ON SCHEMA product_schema TO iftrue;
GRANT ALL PRIVILEGES ON SCHEMA order_schema TO iftrue;
GRANT ALL PRIVILEGES ON SCHEMA delivery_schema TO iftrue;
GRANT ALL PRIVILEGES ON SCHEMA hub_schema TO iftrue;
GRANT ALL PRIVILEGES ON SCHEMA notification_schema TO iftrue;