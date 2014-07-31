INSERT INTO RegisteredServiceImpl (allowedToProxy, anonymousAccess, name, enabled, evaluation_order, ignoreAttributes, description, serviceId, ssoEnabled, theme, expression_type, username_attr)
VALUES (0, 0, 'CAM', 1, 95000, 1, 'CAM', 'https://cam\\.infusion(soft|test)\\.com.*', 1, NULL, 'regex', 'id')
ON DUPLICATE KEY UPDATE name = 'CAM';