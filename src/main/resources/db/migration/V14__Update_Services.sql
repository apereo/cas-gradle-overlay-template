UPDATE RegisteredServiceImpl set serviceId = '((https://.+\\.customerhub(\\.net|test\\.com))|(http://.+\\.customerhub.(dev|local)))(:[0-9]+)?/.*' where name = 'CustomerHub';

UPDATE RegisteredServiceImpl set username_attr = 'id' where name = 'Marketplace';
