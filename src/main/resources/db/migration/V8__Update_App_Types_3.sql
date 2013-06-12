UPDATE pending_user_account set app_type = upper(app_type) where app_type = lower(app_type);
UPDATE migrated_app set app_type = upper(app_type) where app_type = lower(app_type);