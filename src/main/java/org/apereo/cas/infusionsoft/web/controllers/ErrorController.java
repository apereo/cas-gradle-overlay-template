package org.apereo.cas.infusionsoft.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping
    public String notAuthorized() {
        return "error/notAuthorized";
    }

    @RequestMapping
    public String serverError() {
        return "error/serverError";
    }
}
