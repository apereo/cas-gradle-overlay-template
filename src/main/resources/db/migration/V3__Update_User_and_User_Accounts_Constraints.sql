/**
 * Adds a unique index on the user_account table and updates not null constraints on user_account and user.
 **/

/*
-- This was finally run in V11__Add_LoginAttempt_Status.sql:
ALTER TABLE `user_account` ADD UNIQUE KEY `app_type_name_username` (`app_type`, `app_name`, `app_username`);
*/

ALTER TABLE `user_account`
	CHANGE COLUMN `disabled` `disabled` BIT(1) NOT NULL,
	CHANGE COLUMN `user_id` `user_id` BIGINT(20) NOT NULL;

ALTER TABLE `user`
	CHANGE COLUMN `enabled` `enabled` BIT(1) NOT NULL,
	CHANGE COLUMN `username` `username` VARCHAR(120) NOT NULL;
