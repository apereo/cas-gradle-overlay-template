package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.domain.MarketingOptions;

public interface MarketingOptionsService {
    void save(MarketingOptions marketingOptions);
    MarketingOptions fetch();
}
