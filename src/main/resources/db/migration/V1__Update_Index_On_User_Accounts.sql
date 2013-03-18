/**
 * Drops and recreates the unique index on the user_account table. This is to address a bug with
 * unique app names.
 **/
alter table user_account drop key user_id;
alter table user_account add unique key `user_id` (`user_id`, `app_name`, `app_type`);
