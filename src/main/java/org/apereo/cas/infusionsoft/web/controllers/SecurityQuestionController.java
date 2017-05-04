package org.apereo.cas.infusionsoft.web.controllers;

import org.apereo.cas.infusionsoft.domain.SecurityQuestion;
import org.apereo.cas.infusionsoft.services.SecurityQuestionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SecurityQuestionController {

    @Autowired
    private SecurityQuestionService securityQuestionService;
    private static final Logger log = Logger.getLogger(SecurityQuestionController.class);

    @RequestMapping
    public String list(Model model) {
        return prepareModelAndReturnView(model);
    }

    @RequestMapping
    public String create(Model model) {
        SecurityQuestion securityQuestion = new SecurityQuestion();
        model.addAttribute("securityQuestion", securityQuestion);
        model.addAttribute("securityQuestionLinkSelected", "selected");

        return "admin/editSecurityQuestion";
    }

    @RequestMapping("/securityquestion/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        SecurityQuestion securityQuestion = securityQuestionService.fetch(id);
        model.addAttribute("securityQuestion", securityQuestion);
        model.addAttribute("securityQuestionLinkSelected", "selected");

        return "admin/editSecurityQuestion";
    }

    @RequestMapping
    public String update(Model model, SecurityQuestion securityQuestion) {
        try {
            securityQuestionService.save(securityQuestion);
            model.addAttribute("success", "Security question saved successfully!");
        } catch (Exception e) {
            log.error("Failed to update security question", e);
            model.addAttribute("error", e.getMessage());
        }
        return prepareModelAndReturnView(model);
    }

    @RequestMapping("/securityquestion/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        try {
            securityQuestionService.delete(id);
        } catch (Exception e) {
            log.error("Failed to delete security question", e);
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("success", "Security question deleted successfully!");

        return prepareModelAndReturnView(model);
    }

    private String prepareModelAndReturnView(Model model) {
        model.addAttribute("securityQuestions", securityQuestionService.fetchAll());
        model.addAttribute("securityQuestionLinkSelected", "selected");

        return "admin/listSecurityQuestions";
    }

}
