UPDATE RegisteredServiceImpl set
	serviceId = 'https?://(help(center)?\\.infusionsoft|dev-help-center\\.gotpantheon)\\.com/.*'
where name = 'HelpCenter';