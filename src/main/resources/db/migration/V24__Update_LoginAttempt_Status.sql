-- Add new security question enums
ALTER TABLE login_attempt CHANGE COLUMN status status ENUM ('AccountLocked', 'BadPassword', 'DisabledUser', 'NoSuchUser', 'PasswordExpired', 'Success', 'OldPassword', 'UnlockedByAdmin', 'PasswordReset', 'SecurityQuestionsOptional', 'SecurityQuestionsRequired') NOT NULL;
