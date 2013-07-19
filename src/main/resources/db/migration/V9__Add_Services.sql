DELETE FROM RegisteredServiceImpl;
INSERT INTO RegisteredServiceImpl (allowedToProxy, anonymousAccess, name, enabled, evaluation_order, ignoreAttributes, description, serviceId, ssoEnabled, theme, expression_type, username_attr)
VALUES
    (0, 0, 'CAS', 1, 1000, 1, 'CAS', 'https://(signin|devcas)\\.infusion(test|soft)\\.com(:[0-9]+)?/.*', 1, NULL, 'regex', 'id'),
    (0, 0, 'Community', 1, 2000, 1, 'Community', 'https?://community\\.infusionsoft\\.com/.*', 1, NULL, 'regex', 'email'),
    (0, 0, 'HelpCenter', 1, 3000, 1, 'Help Center', 'https://helpcenter\\.infusionsoft\\.com/.*', 1, NULL, 'regex', 'email'),
    (0, 0, 'Marketplace', 1, 4000, 1, 'Marketplace', 'https?://(marketplace\\.infusionsoft\\.com|((infusionsft-market|infusionsoftmarketdev)\\.staging-preview\\.com))/.*', 1, NULL, 'regex', 'email'),
    (0, 0, 'OnlineLearning', 1, 5000, 1, 'Online Learning (Canvas)', 'https://(.+\\.)?onlinelearning\\.infusionsoft\\.com/.*', 1, NULL, 'regex', 'email'),
    (0, 0, 'CustomerHub', 1, 80000, 1, 'Customer Hub', 'https://.+\\.customerhub(\\.net|test\\.com)/.*', 1, NULL, 'regex', 'email'),
    (0, 0, 'InfusionsoftCRM', 1, 90000, 1, 'Infusionsoft CRM', 'https://.+\\.infusion(soft|test)\\.com(:[0-9]+)?/.*', 1, NULL, 'regex', 'email');