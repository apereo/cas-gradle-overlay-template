package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MasheryController {

    @Autowired
    private MasheryApiClientService masheryApiClientService;

    @RequestMapping
    @ResponseBody
    public String clearCaches() {
        masheryApiClientService.clearCaches();
        return "Caches cleared";
    }
}
