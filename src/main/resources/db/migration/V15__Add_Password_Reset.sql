-- Add PasswordReset to the set of enum values
ALTER TABLE login_attempt CHANGE COLUMN status status ENUM ('AccountLocked', 'BadPassword', 'DisabledUser', 'NoSuchUser', 'PasswordExpired', 'PasswordReset', 'Success', 'OldPassword', 'UnlockedByAdmin') NOT NULL;

