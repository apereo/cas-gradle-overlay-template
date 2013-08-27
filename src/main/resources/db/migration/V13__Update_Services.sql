INSERT INTO RegisteredServiceImpl (allowedToProxy, anonymousAccess, name, enabled, evaluation_order, ignoreAttributes, description, serviceId, ssoEnabled, theme, expression_type, username_attr)
VALUES
    (1, 0, 'Marketplace3', 1, 4100, 1, 'Marketplace 3+', 'https://marketplace3(dev)?\\.infusion(soft|test)\\.com(:[0-9]+)?/.*', 1, NULL, 'regex', 'id'),
    (0, 0, 'Mashery', 1, 6000, 1, 'Infusionsoft Mashery Dev Portal', 'https?://infusionsoft\\.mashery\\.com/.*', 1, NULL, 'regex', 'id');

-- The old name for the service:
DELETE from RegisteredServiceImpl WHERE name = 'Infusionsoft Mashery Dev Portal';

UPDATE RegisteredServiceImpl SET allowedToProxy = 1 WHERE name in ('InfusionsoftCRM', 'Marketplace3');