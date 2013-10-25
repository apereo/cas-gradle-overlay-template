-- Migrate data from the old column to the new column
-- success column was removed
-- UPDATE login_attempt set status = if (success, 'Success', 'BadPassword');

-- Change type from string to enum, and make it not null
ALTER TABLE login_attempt CHANGE COLUMN status status ENUM ('AccountLocked', 'BadPassword', 'DisabledUser', 'NoSuchUser', 'PasswordExpired', 'Success', 'OldPassword', 'UnlockedByAdmin') NOT NULL;

-- Add the constraint that was planned for V3__Update_User_and_User_Accounts_Constraints.sql
ALTER TABLE user_account ADD UNIQUE KEY app_type_name_username (app_type, app_name, app_username);
