package org.apereo.cas.infusionsoft.web.controllers;

import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.services.SecurityService;
import org.apereo.cas.infusionsoft.services.UserService;
import org.apereo.cas.infusionsoft.web.controllers.commands.DiscourseSSOCommand;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.crypto.Mac;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Controller
public class DiscourseController {

    private static final Logger log = Logger.getLogger(DiscourseController.class);

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    @Value("${infusionsoft.discourse.secret}")
    String secret;

    /**
     * Process SSO from discourse (community.infusionsoft.com)
     *
     * @param command command
     * @param model   model
     * @return String view
     */
    @RequestMapping
    public String sso(@ModelAttribute("discourseSSOCommand") DiscourseSSOCommand command, Model model) throws Exception {
        User user = securityService.getCurrentUser();
        user = userService.loadUser(user.getUsername());

        String payloadBase64 = command.getSso();
        String rawPayload = new String(Base64.decodeBase64(payloadBase64));
        String[] splitPayload = StringUtils.split(rawPayload, '&');
        String nonce = StringUtils.split(splitPayload[0], '=')[1];
        String returnUrl = URLDecoder.decode(StringUtils.split(splitPayload[1], '=')[1], "UTF-8");

        Mac mac = HmacUtils.getHmacSha256(secret.getBytes());
        String calculatedSignature = Hex.encodeHexString(mac.doFinal(command.getSso().getBytes()));

        if (calculatedSignature.equals(command.getSig())) {
            StringBuilder newPayload = new StringBuilder();
            newPayload.append("nonce=").append(nonce).append("&");
            newPayload.append("email=").append(URLEncoder.encode(user.getUsername(), "UTF-8")).append("&");
            newPayload.append("require_activation=").append(true).append("&");
            newPayload.append("external_id=").append(user.getId()).append("&");
            newPayload.append("name=").append(URLEncoder.encode(user.getFirstName() + " " + user.getLastName(), "UTF-8"));

            String newPayloadBase64Encoded = Base64.encodeBase64String(newPayload.toString().getBytes("UTF-8"));

            String newSignature = Hex.encodeHexString(mac.doFinal(newPayloadBase64Encoded.getBytes("UTF-8")));

            model.addAttribute("sso", newPayloadBase64Encoded);
            model.addAttribute("sig", newSignature);

            return "redirect:" + returnUrl;
        } else {
            log.error("Discourse SSO signature failure");
            throw new Exception("Signature didn't validate");
        }
    }
}
