package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.dao.MarketingOptionsDAO;
import org.apereo.cas.infusionsoft.domain.MarketingOptions;
import org.springframework.transaction.annotation.Transactional;

@Transactional(transactionManager = "transactionManager")
public class MarketingOptionsServiceImpl implements MarketingOptionsService {

    private MarketingOptionsDAO marketingOptionsDAO;

    public MarketingOptionsServiceImpl(MarketingOptionsDAO marketingOptionsDAO) {
        this.marketingOptionsDAO = marketingOptionsDAO;
    }

    public void save(MarketingOptions marketingOptions){
        marketingOptionsDAO.save(marketingOptions);
    }

    public MarketingOptions fetch() {
        Iterable<MarketingOptions> options = marketingOptionsDAO.findAll();
        if (options.iterator().hasNext()) {
            return options.iterator().next();
        } else {
            return new MarketingOptions();
        }
    }
}
