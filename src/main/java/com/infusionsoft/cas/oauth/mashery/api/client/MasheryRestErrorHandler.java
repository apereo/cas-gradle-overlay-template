package com.infusionsoft.cas.oauth.mashery.api.client;

import com.infusionsoft.cas.oauth.exceptions.OAuthServerErrorException;
import com.infusionsoft.cas.oauth.exceptions.OAuthUnauthorizedClientException;
import com.infusionsoft.cas.oauth.mashery.api.wrappers.MasheryResult;
import org.apache.commons.lang3.text.StrBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

/**
 * The purpose of this class is to take an exception thrown by the Mashery API and:
 * 1) Deserialize the error result
 * 2) Log pertinent info like headers and messages
 * 3) Throw out own specific OAuthException based on error code
 */
public class MasheryRestErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(MasheryRestErrorHandler.class);

    private static final int MASHERY_BAD_CLIENT_ID = -2001;

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        MasheryResult masheryResult = objectMapper.readValue(response.getBody(), MasheryResult.class);

        HttpHeaders headers = response.getHeaders();
        StrBuilder sb = new StrBuilder();

        sb.appendNewLine();
        for (String key : headers.keySet()) {
            sb.append(key).append("=").appendln(headers.get(key));
        }

        log.error(new StrBuilder("Error calling mashery=").appendln(masheryResult.getError().toString()).append("Headers from response=").append(sb.toString()).toString());

        if (masheryResult.getError() != null) {

            int errorCode = Integer.parseInt(masheryResult.getError().getCode());

            switch (errorCode) {
                case MASHERY_BAD_CLIENT_ID:
                    throw new OAuthUnauthorizedClientException();

                default:
                    throw new OAuthServerErrorException();

            }
        } else {
            super.handleError(response);
        }
    }
}
