package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.domain.AppType;
import com.infusionsoft.cas.domain.UserAccount;
import com.infusionsoft.cas.oauth.dto.OAuthUserApplication;
import com.infusionsoft.cas.oauth.exceptions.OAuthException;
import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiClientService;
import com.infusionsoft.cas.oauth.mashery.api.domain.MasheryAccessToken;
import com.infusionsoft.cas.oauth.services.OAuthService;
import com.infusionsoft.cas.services.CrmService;
import com.infusionsoft.cas.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

@Controller
public class MasheryController {

    @Value("${mashery.api.crm.service.key}")
    private String crmServiceKey;

    @Value("${infusionsoft.crm.domain}")
    private String crmDomain;

    @Autowired
    private CrmService crmService;

    @Autowired
    private MasheryApiClientService masheryApiClientService;

    @Autowired
    private UserService userService;

    @Autowired
    private OAuthService oAuthService;

    @RequestMapping
    @ResponseBody
    public String clearCaches() {
        masheryApiClientService.clearCaches();
        return "Caches cleared";
    }

    /**
     * Admin Level User Application Searching
     *
     * @throws OAuthException
     */
    @RequestMapping
    public String userApplicationSearch(Model model, String search, String username, String appName, String success, String error) throws OAuthException {
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(appName)) {

            UserAccount userAccount = userService.findUserAccountByInfusionsoftId(appName, AppType.CRM, username);
            Set<OAuthUserApplication> userApplications = oAuthService.fetchUserApplicationsByUserAccount(crmServiceKey, userAccount);

            model.addAttribute("userApplications", userApplications);
            model.addAttribute("username", username);
            model.addAttribute("appName", appName);
            model.addAttribute("search", search);
            model.addAttribute("appUrl", crmService.buildCrmUrl(appName));
            model.addAttribute("success", success);
            model.addAttribute("error", error);
        }

        model.addAttribute("oauthLinkSelected", "selected");

        return "mashery/userApplicationSearch";
    }
    /**
     * Admin Level Access Token Searching
     *
     * @throws com.infusionsoft.cas.oauth.exceptions.OAuthException
     */
    @RequestMapping
    public String viewAccessToken(Model model, String accessToken, String appName, String username) throws OAuthException {
        if (StringUtils.isNotBlank(accessToken)) {
            MasheryAccessToken masheryAccessToken = masheryApiClientService.fetchAccessToken(crmServiceKey, accessToken);

            model.addAttribute("masheryAccessToken", masheryAccessToken);
            model.addAttribute("appName", appName);
            model.addAttribute("username", username);
        }

        return "mashery/viewAccessToken";
    }

    @RequestMapping
    public String revokeAccessToken(Model model, String username, String appName, String clientId) {
        if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(appName) && StringUtils.isNotBlank(clientId)) {
            UserAccount userAccount = userService.findUserAccountByInfusionsoftId(appName, AppType.CRM, username);

            model.addAttribute("username", username);
            model.addAttribute("appName", appName);

            try {
                Set<OAuthUserApplication> userApplications = oAuthService.fetchUserApplicationsByUserAccount(crmServiceKey, userAccount);

                for(OAuthUserApplication oAuthUserApplication : userApplications) {
                    if(clientId.equals(oAuthUserApplication.getClientId())) {
                        for(String accessToken : oAuthUserApplication.getAccessTokens()) {
                            oAuthService.revokeAccessToken(crmServiceKey, clientId, accessToken);
                        }
                    }
                }

                model.addAttribute("success", "Access tokens revoked");
            } catch (OAuthException e) {
                model.addAttribute("error", e.getErrorDescription());
            }
        }

        return "redirect:userApplicationSearch";
    }

    @RequestMapping
    public void testApiCall(Model model, String accessToken) throws MalformedURLException{

        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("https://api." + crmDomain + "/crm/xmlrpc/v1?access_token=" + accessToken));

            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            Object[] params = {"hello"};

            client.execute("DataService.echo", params);
            model.addAttribute("success", true);
        } catch (XmlRpcException e) {
            model.addAttribute("responseText", e.toString());
            model.addAttribute("success", false);
        } catch (MalformedURLException e) {
            model.addAttribute("responseText", e.toString());
            model.addAttribute("success", false);
        }
    }
}
