
ALTER TABLE RegisteredServiceImpl ADD UNIQUE INDEX UK_REGISTERED_SERVICE_NAME (name);

INSERT INTO RegisteredServiceImpl (allowedToProxy, anonymousAccess, name, enabled, evaluation_order, ignoreAttributes, description, serviceId, ssoEnabled, theme, expression_type, username_attr)
VALUES (0, 0, 'CustomerCenter', 1, 7000, 1, 'Infusionsoft Customer Center', 'https://setup(dev)?\\.infusionsoft\\.com/.*', 1, NULL, 'regex', 'id')
ON DUPLICATE KEY UPDATE name = 'CustomerCenter';

