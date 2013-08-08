-- Migrate data from the old column to the new column
UPDATE login_attempt set status = if (success, 'Success', 'BadPassword');

-- Change type from string to enum, and make it not null
ALTER TABLE login_attempt CHANGE COLUMN status status ENUM ('AccountLocked', 'BadPassword', 'DisabledUser', 'NoSuchUser', 'PasswordExpired', 'Success', 'OldPassword', 'UnlockedByAdmin') NOT NULL;

ALTER TABLE login_attempt DROP COLUMN success;
