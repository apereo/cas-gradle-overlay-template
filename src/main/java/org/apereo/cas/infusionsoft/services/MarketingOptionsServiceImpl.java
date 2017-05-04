package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.dao.MarketingOptionsDAO;
import org.apereo.cas.infusionsoft.domain.MarketingOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("marketingOptionsService")
@Transactional
public class MarketingOptionsServiceImpl implements MarketingOptionsService{
    @Autowired
    private MarketingOptionsDAO marketingOptionsDAO;

    public void save(MarketingOptions marketingOptions){
        marketingOptionsDAO.save(marketingOptions);
    }

    public MarketingOptions fetch(){
        Iterable<MarketingOptions> options = marketingOptionsDAO.findAll();
        if(options.iterator().hasNext()){
            return options.iterator().next();
        } else {
            return new MarketingOptions();
        }
    }
}
