package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.MarketingOptions;
import com.infusionsoft.cas.services.MarketingOptionsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MarketingOptionsController {
    @Autowired
    private MarketingOptionsService marketingOptionsService;
    private static final Logger log = Logger.getLogger(MarketingOptionsController.class);

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
