package com.infusionsoft.cas.services;

import com.infusionsoft.cas.domain.MarketingOptions;

public interface MarketingOptionsService {
    void save(MarketingOptions marketingOptions);
    MarketingOptions fetch();
}
