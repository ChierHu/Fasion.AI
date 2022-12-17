--liquibase formatted sql
--changeset chenbaining:2
-- update column 'status' type by user_info table
ALTER TABLE user_info ALTER COLUMN "status" TYPE varchar(20);
--preconditions onFail:CONTINUE onError:CONTINUE
--precondition-sql-check expectedResult:0 select count(*) from information_schema.columns WHERE table_schema = 'public' and table_name = 'user_info' and column_name = 'meta';
ALTER TABLE user_info ADD "meta" json;
-- delete all info by user_extra
DELETE FROM user_extra WHERE 1=1;
-- update the value of the status field in the user_info table to 'active'
UPDATE user_info SET status = 'active' WHERE 1=1;