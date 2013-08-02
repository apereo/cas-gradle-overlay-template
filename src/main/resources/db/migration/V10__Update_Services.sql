UPDATE RegisteredServiceImpl set serviceId = 'https?://community\\.infusion(soft|test)\\.com/.*' where name = 'Community';
UPDATE RegisteredServiceImpl set serviceId = 'https?://helpcenter\\.infusionsoft\\.com/.*' where name = 'HelpCenter';
UPDATE RegisteredServiceImpl set serviceId = '((https://.+\\.customerhub(\\.net|test\\.com))|(http://.+\\.customerhub.(dev|local)))/.*' where name = 'CustomerHub';
