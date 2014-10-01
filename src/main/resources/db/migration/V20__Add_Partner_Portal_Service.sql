UPDATE RegisteredServiceImpl set
	serviceId = 'https://cam\\.infusion(soft|test)\\.com/.*',
	evaluation_order = 8000
where name = 'CAM';

UPDATE RegisteredServiceImpl set
	serviceId = 'https?://(helpcenter\\.infusionsoft|dev-help-center\\.gotpantheon)\\.com/.*'
where name = 'HelpCenter';

INSERT INTO RegisteredServiceImpl (allowedToProxy, anonymousAccess, name, enabled, evaluation_order, ignoreAttributes, description, serviceId, ssoEnabled, theme, expression_type, username_attr)
VALUES (0, 0, 'PartnerPortal', 1, 9000, 1, 'Partner Portal', 'https://(portal\\.infusionsoft|dev-is-portal\\.gotpantheon)\\.com/.*', 1, NULL, 'regex', 'id')
ON DUPLICATE KEY UPDATE name = 'PartnerPortal';