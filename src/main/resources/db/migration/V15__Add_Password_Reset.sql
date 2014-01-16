-- Add PasswordReset to the set of enum values
ALTER TABLE login_attempt CHANGE COLUMN status status ENUM ('AccountLocked', 'BadPassword', 'DisabledUser', 'NoSuchUser', 'PasswordExpired', 'Success', 'OldPassword', 'UnlockedByAdmin', 'PasswordReset') NOT NULL;

