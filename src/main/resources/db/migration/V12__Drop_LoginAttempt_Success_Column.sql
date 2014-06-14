
-- Deferred from V11__Add_LoginAttempt_Status.sql to allow upgrades with multiple nodes (since any nodes not upgraded would start to fail when the first node was upgraded)
ALTER TABLE login_attempt DROP COLUMN success;
