package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.api.domain.APIErrorDTO;
import com.infusionsoft.cas.api.domain.ApplicationDTO;
import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiClientService;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryApplication;
import com.infusionsoft.cas.services.SecurityService;
import com.infusionsoft.cas.support.AppHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/api/mashery")
public class ApiMasheryController {

    @Autowired
    private MasheryApiClientService masheryApiClientService;

    @Autowired
    private AppHelper appHelper;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SecurityService securityService;

    /**
     * A simple REST endpoint for getting applications based on api key.
     *
     * @param apiKey apiKey
     * @param locale locale
     * @return ResponseEntity
     */
    @RequestMapping(value = "/applications", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity get(String apiKey, Locale locale) {
        try {
            final Set<MasheryApplication> applications = masheryApiClientService.fetchApplicationsByAPIKey(apiKey);
            Set<ApplicationDTO> dtos = new HashSet<>();
            for(MasheryApplication application : applications){
                dtos.add(new ApplicationDTO(application.getId(), application.getName(), application.getUuid()));
            }
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new APIErrorDTO("cas.exception.general", messageSource, new Object[]{e.getMessage()}, locale), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
