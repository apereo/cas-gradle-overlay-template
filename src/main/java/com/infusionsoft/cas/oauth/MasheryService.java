package com.infusionsoft.cas.oauth;

import com.infusionsoft.cas.oauth.domain.*;
import com.infusionsoft.cas.oauth.wrappers.WrappedMasheryApplication;
import com.infusionsoft.cas.oauth.wrappers.WrappedMasheryAuthorizationCode;
import com.infusionsoft.cas.oauth.wrappers.WrappedMasheryMember;
import com.infusionsoft.cas.oauth.wrappers.WrappedMasheryOAuthApplication;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to communicate with the Mashery API
 */
public class MasheryService {

    @Value("${mashery.api.url}")
    private String apiUrl;

    @Value("${mashery.api.key}")
    private String apiKey;

    @Value("${mashery.api.secret}")
    private String apiSecret;

    @Value("${mashery.service.key}")
    private String serviceKey;

    @Value("${mashery.site.id}")
    private String siteId;

    private static final Logger log = Logger.getLogger(MasheryService.class);

    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> httpMessageConverters = new ArrayList<HttpMessageConverter<?>>();
        httpMessageConverters.add(new MappingJacksonHttpMessageConverter());
        restTemplate.setMessageConverters(httpMessageConverters);

        restTemplate.setErrorHandler(new MasheryRestErrorHandler());

        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
    }

    private String buildUrl() {
        //Adapted from Grails App, might not work yet
        String retVal = null;
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("Unable to get an MD5 MessageDigest", e);
        }

        if (messageDigest != null) {
            Long epoch = new Double(Math.floor(System.currentTimeMillis() / 1000)).longValue();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(apiKey).append(apiSecret).append(epoch);

            messageDigest.update(stringBuilder.toString().getBytes());
            String md5 = new BigInteger(1, messageDigest.digest()).toString(16);

            retVal = apiUrl + "/" + siteId + "?apikey=" + apiKey + "&sig=" + md5;
        }

        return retVal;
    }

    MasheryOAuthApplication fetchOAuthApplication(String clientId, String redirectUri, String responseType) {
        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("oauth2.fetchApplication");

        masheryJsonRpcRequest.getParams().add(serviceKey);

        masheryJsonRpcRequest.getParams().add(new MasheryClient(clientId, ""));
        masheryJsonRpcRequest.getParams().add(new MasheryUri(redirectUri, ""));
        masheryJsonRpcRequest.getParams().add(responseType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryOAuthApplication wrappedMasheryOAuthApplication = restTemplate.postForObject(buildUrl(), request, WrappedMasheryOAuthApplication.class);

        return wrappedMasheryOAuthApplication.getResult();
    }

    MasheryApplication fetchApplication(Integer id) {
        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("application.fetch");

        masheryJsonRpcRequest.getParams().add(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryApplication wrappedMasheryOAuthApplication = restTemplate.postForObject(buildUrl(), request, WrappedMasheryApplication.class);

        return wrappedMasheryOAuthApplication.getResult();
    }

    MasheryMember fetchMember(String username) {
        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("member.fetch");

        masheryJsonRpcRequest.getParams().add(username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryMember wrappedMasheryOAuthApplication = restTemplate.postForObject(buildUrl(), request, WrappedMasheryMember.class);

        return wrappedMasheryOAuthApplication.getResult();
    }

    MasheryAuthorizationCode createAuthorizationCode(String clientId, String scope, String redirectUri, String username) {
        String userContext = username + "|" + scope;

        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("oauth2.createAuthorizationCode");
        masheryJsonRpcRequest.getParams().add(serviceKey);
        masheryJsonRpcRequest.getParams().add(new MasheryClient(clientId, ""));
        masheryJsonRpcRequest.getParams().add(new MasheryUri(redirectUri, ""));
        masheryJsonRpcRequest.getParams().add(scope);
        masheryJsonRpcRequest.getParams().add(userContext);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryAuthorizationCode wrappedMasheryAuthorizationCode = restTemplate.postForObject(buildUrl(), request, WrappedMasheryAuthorizationCode.class);

        return wrappedMasheryAuthorizationCode != null ? wrappedMasheryAuthorizationCode.getResult() : null;
    }

}
