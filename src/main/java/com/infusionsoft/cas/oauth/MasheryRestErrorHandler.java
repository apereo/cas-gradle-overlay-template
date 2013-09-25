package com.infusionsoft.cas.oauth;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

public class MasheryRestErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger log = Logger.getLogger(MasheryRestErrorHandler.class);

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        MasheryResult masheryResult = objectMapper.readValue(response.getBody(), MasheryResult.class);

        HttpHeaders headers = response.getHeaders();
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("line.separator"));
        for(String key: headers.keySet()) {
            sb.append(key);
            sb.append("=");
            sb.append(headers.get(key));
            sb.append(System.getProperty("line.separator"));
        }
        log.error("Error calling mashery=" + masheryResult.getError().toString() + " Headers from response=" + sb.toString());
        super.handleError(response);
    }
}
