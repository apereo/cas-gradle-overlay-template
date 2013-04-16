package com.infusionsoft.cas.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping
    public String notAuthorized() {
        return "error/notAuthorized";
    }
}
