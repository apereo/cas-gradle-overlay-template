package org.apereo.cas.infusionsoft.web.controllers;

import org.apereo.cas.infusionsoft.domain.MarketingOptions;
import org.apereo.cas.infusionsoft.services.MarketingOptionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MarketingOptionsController {
    @Autowired
    private MarketingOptionsService marketingOptionsService;
    private static final Logger log = LoggerFactory.getLogger(MarketingOptionsController.class);

    @RequestMapping
    public String show(Model model) {
        MarketingOptions marketingOptions = marketingOptionsService.fetch();
        model.addAttribute("marketingOptions", marketingOptions);
        model.addAttribute("marketingOptionsLinkSelected", "selected");
        return "admin/editMarketingOptions";
    }

    @RequestMapping
    public String update(Model model, MarketingOptions marketingOptions) {
        try {
            marketingOptionsService.save(marketingOptions);
            model.addAttribute("success", "Marketing options saved successfully!");
            model.addAttribute("marketingOptionsLinkSelected", "selected");
        } catch (Exception e) {
            log.error("Failed to update Marketing Options", e);
            model.addAttribute("error", e.getMessage());
        }
        return prepareModelAndReturnView(model);
    }

    private String prepareModelAndReturnView(Model model) {
        model.addAttribute("marketingOptions", marketingOptionsService.fetch());
        return "admin/editMarketingOptions";
    }


}
