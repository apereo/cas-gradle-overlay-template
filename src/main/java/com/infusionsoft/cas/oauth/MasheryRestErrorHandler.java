package com.infusionsoft.cas.oauth;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

public class MasheryRestErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger log = Logger.getLogger(MasheryRestErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        MasheryResult masheryResult = objectMapper.readValue(response.getBody(), MasheryResult.class);

        log.error(masheryResult.getError().toString());

        super.handleError(response);
    }
}
