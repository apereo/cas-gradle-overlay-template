UPDATE RegisteredServiceImpl set
 name = 'LegacyMarketplace',
 description = 'Legacy KWall Marketplace',
 serviceId = 'https?://(infusionsoft\\.production-preview|infusionsoft-market\\.staging-preview)\\.com/.*'
where name = 'Marketplace' and evaluation_order = 4000;

UPDATE RegisteredServiceImpl set
 name = 'MarketplaceAPI',
 description = 'Marketplace API',
 serviceId = 'https://marketplace3(dev)?\\.infusion(soft|test)\\.com(:[0-9]+)?/.*'
where name = 'Marketplace3';

UPDATE RegisteredServiceImpl set
 name = 'Marketplace',
 description = 'Marketplace Polymer UI',
 serviceId = 'https://marketplace(3(dev)?ui)?\\.infusion(soft|test)\\.com(:[0-9]+)?((/.*)|$)'
where name = 'MarketplacePolymer';

