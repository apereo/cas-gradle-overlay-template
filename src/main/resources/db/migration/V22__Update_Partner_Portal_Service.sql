UPDATE RegisteredServiceImpl set
 serviceId = 'https://(((dev|test|live)-is-partners\\.pantheon\\.io)|(portal\\.infusionsoft\\.com))/.*'
where name = 'PartnerPortal';
