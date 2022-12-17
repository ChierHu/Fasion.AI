--liquibase formatted sql
--changeset chenbaining:4
ALTER TABLE task ADD IF NOT EXISTS "sku_id" json;
DELETE FROM payment WHERE 1=1;
DELETE FROM purchase WHERE 1=1;
ALTER TABLE IF EXISTS payment ADD IF NOT EXISTS "depends_on" varchar;
ALTER TABLE IF EXISTS purchase DROP IF EXISTS "payment_id";
ALTER TABLE IF EXISTS purchase ADD IF NOT EXISTS "payments" character varying[];
ALTER TABLE IF EXISTS payment ADD IF NOT EXISTS "slot" varchar;
--preconditions onFail:CONTINUE onError:CONTINUE
INSERT INTO sku (id, sku, revision, name, price, type, slogan, status, props) VALUES ('Qy2ek0nVPj', 'XvZpMj02P6', 1, '执行换脸任务', 10, 'product', '执行换脸任务', 'enable', '{"points": -1}');
--preconditions onFail:CONTINUE onError:CONTINUE
INSERT INTO sku (id, sku, revision, name, price, type, slogan, status, props) VALUES ('YL3P7j8bQL', '25Ym78Lz1Y', 1, '执行换背景任务', 10, 'product', '执行换背景任务', 'enable', '{"points": -1}');