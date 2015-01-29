
INSERT INTO RegisteredServiceImpl (allowedToProxy, anonymousAccess, name, enabled, evaluation_order, ignoreAttributes, description, serviceId, ssoEnabled, theme, expression_type, username_attr)
VALUES (0, 0, 'MarketplacePolymer', 1, 4200, 1, 'Marketplace Polymer UI', 'https://marketplace3(dev)?ui\\.infusion(soft|test)\\.com(:[0-9]+)?((/.*)|$)', 1, NULL, 'regex', 'id')
ON DUPLICATE KEY UPDATE name = 'MarketplacePolymer', description = 'Marketplace Polymer UI', serviceId = 'https://marketplace3(dev)?ui\\.infusion(soft|test)\\.com(:[0-9]+)?((/.*)|$)';
