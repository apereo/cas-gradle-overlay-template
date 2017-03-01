package com.infusionsoft.cas.web.controllers;

import com.infusionsoft.cas.api.domain.APIErrorDTO;
import com.infusionsoft.cas.oauth.dto.OAuthApplication;
import com.infusionsoft.cas.oauth.exceptions.OAuthServerErrorException;
import com.infusionsoft.cas.oauth.exceptions.OAuthUnauthorizedClientException;
import com.infusionsoft.cas.oauth.mashery.api.client.MasheryApiException;
import com.infusionsoft.cas.oauth.services.OAuthService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import java.util.Locale;

@Controller
@RequestMapping("/rest/oauth")
public class ApiOAuthController {

    private static final Logger log = Logger.getLogger(ApiOAuthController.class);
    public static final int MASHERY_ERROR_CODE_FORBIDDEN = 4000;

    @Value("${infusionsoft.cas.apikey}")
    private String requiredApiKey;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private MessageSource messageSource;

    /**
     * A simple REST endpoint for getting OAuth Application information.
     *
     * @param serviceKey serviceKey
     * @param clientId   clientId
     * @param locale     locale
     * @return ResponseEntity
     */
    @ResponseBody
    @RequestMapping(value = "/service/{serviceKey}/client/{clientId}", method = RequestMethod.GET)
    public ResponseEntity getOAuthAppInfo(@PathVariable("serviceKey") String serviceKey, @PathVariable("clientId") String clientId, String apiKey, Locale locale) {
        // Validate the API key
        ResponseEntity apiKeyResponse = validateApiKey(apiKey, locale);
        if (apiKeyResponse != null) {
            return apiKeyResponse;
        }

        try {
            final OAuthApplication oAuthApplication = oAuthService.fetchApplication(serviceKey, clientId, null, null);
            return new ResponseEntity<>(oAuthApplication, HttpStatus.OK);
        } catch (OAuthUnauthorizedClientException e) {
            // This exception happens if the client ID is wrong
            return new ResponseEntity<>(new APIErrorDTO("cas.exception.clientId.notFound", messageSource, new Object[]{clientId}, locale), HttpStatus.NOT_FOUND);
        } catch (OAuthServerErrorException e) {
            if (e.getCause() instanceof RestClientException) {
                final RestClientException restClientException = (RestClientException) e.getCause();
                if (restClientException.getCause() instanceof MasheryApiException) {
                    final MasheryApiException masheryApiException = (MasheryApiException) restClientException.getCause();
                    final int errorCode = Integer.parseInt(masheryApiException.getMasheryError().getCode());
                    if (errorCode == MASHERY_ERROR_CODE_FORBIDDEN) {
                        // Mashery returns this if the service key is invalid
                        return new ResponseEntity<>(new APIErrorDTO("cas.exception.serviceKey.notFound", messageSource, new Object[]{serviceKey}, locale), HttpStatus.NOT_FOUND);
                    }
                }
            }
            return createErrorResponse(e, serviceKey, clientId, locale);
        } catch (Exception e) {
            return createErrorResponse(e, serviceKey, clientId, locale);
        }
    }

    private ResponseEntity createErrorResponse(Exception e, String serviceKey, String clientId, Locale locale) {
        final APIErrorDTO error = new APIErrorDTO("cas.exception.getAppInfo.failure", messageSource, new Object[]{serviceKey, clientId}, locale);
        log.error(error.getMessage(), e);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<APIErrorDTO> validateApiKey(String apiKey, Locale locale) {
        // Validate the API key
        // TODO: replace this with a separate Spring security authentication entry point so the logic doesn't have to be embedded here
        if (!requiredApiKey.equals(apiKey)) {
            log.warn("Invalid API access: apiKey = " + apiKey);
            return new ResponseEntity<>(new APIErrorDTO("cas.exception.invalid.apikey", messageSource, locale), HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

}
