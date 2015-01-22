package com.infusionsoft.cas.oauth.mashery.api.client;

import com.infusionsoft.cas.oauth.exceptions.OAuthException;
import com.infusionsoft.cas.oauth.exceptions.OAuthServerErrorException;
import com.infusionsoft.cas.oauth.exceptions.OAuthUnauthorizedClientException;
import com.infusionsoft.cas.oauth.mashery.api.domain.*;
import com.infusionsoft.cas.oauth.mashery.api.wrappers.*;
import com.infusionsoft.cas.support.InfusionsoftObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Service to communicate with the Mashery API
 */
@Service
public class MasheryApiClientService {

    private static final Logger log = LoggerFactory.getLogger(MasheryApiClientService.class);
    private static final int MASHERY_BAD_CLIENT_ID = -2001;

    @Value("${mashery.api.url}")
    private String apiUrl;

    @Value("${mashery.api.key}")
    private String apiKey;

    @Value("${mashery.api.secret}")
    private String apiSecret;

    @Value("${mashery.site.id}")
    private String siteId;

    @Autowired
    InfusionsoftObjectMapper infusionsoftObjectMapper;

    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> httpMessageConverters = new ArrayList<HttpMessageConverter<?>>();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(infusionsoftObjectMapper);

        httpMessageConverters.add(converter);

        restTemplate.setMessageConverters(httpMessageConverters);
        restTemplate.setErrorHandler(new MasheryRestErrorHandler());
        restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());
    }

    protected String buildUrl() {
        long epoch = System.currentTimeMillis() / 1000;
        return buildUrl(epoch);
    }

    protected String buildUrl(long epoch) {
        String signature = DigestUtils.md5Hex(apiKey + apiSecret + epoch);
        return StringUtils.join(apiUrl, "/", siteId, "?apikey=", apiKey, "&sig=", signature);
    }

    public Set<MasheryUserApplication> fetchUserApplicationsByUserContext(String serviceKey, String userContext) throws OAuthException {
        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("oauth2.fetchUserApplications");

        masheryJsonRpcRequest.getParams().add(serviceKey);
        masheryJsonRpcRequest.getParams().add(userContext);
        masheryJsonRpcRequest.getParams().add(TokenStatus.Active.getValue());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryUserApplication wrappedMasheryUserApplication;

        try {
            wrappedMasheryUserApplication = restTemplate.postForObject(buildUrl(), request, WrappedMasheryUserApplication.class);
        } catch (RestClientException e) {
            throw convertException(e);
        }

        return wrappedMasheryUserApplication.getResult();
    }

    public Boolean revokeAccessToken(String serviceKey, String clientId, String accessToken) throws OAuthException {
        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("oauth2.revokeAccessToken");

        masheryJsonRpcRequest.getParams().add(serviceKey);
        Map<String, String> clientMap = new HashMap<String, String>();
        clientMap.put("client_id", clientId);
        masheryJsonRpcRequest.getParams().add(clientMap);
        masheryJsonRpcRequest.getParams().add(accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryBoolean wrappedBooleanResult;

        try {
            wrappedBooleanResult = restTemplate.postForObject(buildUrl(), request, WrappedMasheryBoolean.class);
        } catch (RestClientException e) {
            throw convertException(e);
        }

        return wrappedBooleanResult.getResult();
    }

    @Cacheable(value = "masheryOAuthApplications")
    public MasheryOAuthApplication fetchOAuthApplication(String serviceKey, String clientId, String redirectUri, String responseType) throws OAuthException {
        try {
            log.info("Fetch OAuth Application. {}, {}, {}, {}", serviceKey, clientId, redirectUri, responseType);
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
        } catch (RestClientException e) {
            throw convertException(e);
        }
    }

    @Cacheable(value = "masheryApplications")
    public MasheryApplication fetchApplication(Integer id) throws OAuthException {

        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("application.fetch");

        masheryJsonRpcRequest.getParams().add(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryApplication wrappedMasheryOAuthApplication;

        try {
            wrappedMasheryOAuthApplication = restTemplate.postForObject(buildUrl(), request, WrappedMasheryApplication.class);
        } catch (RestClientException e) {
            throw convertException(e);
        }

        return wrappedMasheryOAuthApplication.getResult();
    }

    @Cacheable(value = "masheryMembers")
    public MasheryMember fetchMember(String username) throws OAuthException {

        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("object.query");

        masheryJsonRpcRequest.getParams().add("SELECT *, roles FROM members WHERE username='" + username + "'");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryMemberQueryResult wrappedMasheryMember;

        try {
            wrappedMasheryMember = restTemplate.postForObject(buildUrl(), request, WrappedMasheryMemberQueryResult.class);
        } catch (RestClientException e) {
            throw convertException(e);
        }

        if(wrappedMasheryMember.getResult().getTotalItems() != 1) {
            throw new OAuthServerErrorException("oauth.exception.missing.member");
        } else {
            return wrappedMasheryMember.getResult().getItems().iterator().next();
        }
    }

    @Cacheable(value = "masheryMembersByClientId")
    public MasheryMember fetchMemberByClientId(String clientId) throws OAuthException {

        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("object.query");

        masheryJsonRpcRequest.getParams().add("SELECT member.*, member.roles.* FROM package_keys WHERE apikey='" + clientId + "'");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryMemberListQueryResult wrappedMasheryMember;

        try {
            wrappedMasheryMember = restTemplate.postForObject(buildUrl(), request, WrappedMasheryMemberListQueryResult.class);
        } catch (RestClientException e) {
            throw convertException(e);
        }

        if(wrappedMasheryMember.getResult().getTotalItems() != 1) {
            throw new OAuthServerErrorException("oauth.exception.missing.member");
        } else {
            return wrappedMasheryMember.getResult().getItems().iterator().next().getMember();
        }
    }

    public MasheryCreateAccessTokenResponse createAccessToken(String serviceKey, String clientId, String clientSecret, String grant_type, String scope, String userContext) throws OAuthException {
        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("oauth2.createAccessToken");

        masheryJsonRpcRequest.getParams().add(serviceKey);
        masheryJsonRpcRequest.getParams().add(new MasheryClient(clientId, clientSecret));
        masheryJsonRpcRequest.getParams().add(new MasheryTokenData(grant_type, scope, null, null, null));
        masheryJsonRpcRequest.getParams().add(new MasheryUri());
        // TODO: ACCESS_TOKEN_TODO: if (userContext != null) ????
        masheryJsonRpcRequest.getParams().add(userContext);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryCreateAccessTokenResponse wrappedMasheryCreateAccessTokenResponse;

        try {
            wrappedMasheryCreateAccessTokenResponse = restTemplate.postForObject(buildUrl(), request, WrappedMasheryCreateAccessTokenResponse.class);
        } catch (RestClientException e) {
            throw convertException(e);
        }

        return wrappedMasheryCreateAccessTokenResponse != null ? wrappedMasheryCreateAccessTokenResponse.getResult() : null;
    }

    public MasheryAccessToken fetchAccessToken(String serviceKey, String accessToken) throws OAuthException {
        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("oauth2.fetchAccessToken");

        masheryJsonRpcRequest.getParams().add(serviceKey);
        masheryJsonRpcRequest.getParams().add(accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryAccessToken wrappedMasheryAccessToken;

        try {
            wrappedMasheryAccessToken = restTemplate.postForObject(buildUrl(), request, WrappedMasheryAccessToken.class);
            wrappedMasheryAccessToken.getResult().setToken(accessToken);
        } catch (RestClientException e) {
            throw convertException(e);
        }

        return wrappedMasheryAccessToken.getResult();
    }


    public MasheryAuthorizationCode createAuthorizationCode(String serviceKey, String clientId, String scope, String redirectUri, String userContext, String state) throws OAuthException {
        MasheryJsonRpcRequest masheryJsonRpcRequest = new MasheryJsonRpcRequest();
        masheryJsonRpcRequest.setMethod("oauth2.createAuthorizationCode");
        masheryJsonRpcRequest.getParams().add(serviceKey);
        masheryJsonRpcRequest.getParams().add(new MasheryClient(clientId, ""));
        masheryJsonRpcRequest.getParams().add(new MasheryUri(redirectUri, state));
        masheryJsonRpcRequest.getParams().add(scope);
        masheryJsonRpcRequest.getParams().add(userContext);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MasheryJsonRpcRequest> request = new HttpEntity<MasheryJsonRpcRequest>(masheryJsonRpcRequest, headers);

        WrappedMasheryAuthorizationCode wrappedMasheryAuthorizationCode;

        try {
            wrappedMasheryAuthorizationCode = restTemplate.postForObject(buildUrl(), request, WrappedMasheryAuthorizationCode.class);
        } catch (RestClientException e) {
            throw convertException(e);
        }

        return wrappedMasheryAuthorizationCode != null ? wrappedMasheryAuthorizationCode.getResult() : null;
    }

    @CacheEvict(value = {"masheryOAuthApplications", "masheryApplications", "masheryMembers", "masheryMembersByClientId"}, allEntries = true)
    public void clearCaches() {
        //The annotation will cause the caches to clear
    }

    private OAuthException convertException(RestClientException e) {
        log.error("Error contacting Mashery", e);

        if (e.getCause() != null && e.getCause() instanceof MasheryApiException) {
            MasheryApiException masheryApiException = (MasheryApiException) e.getCause();

            int errorCode = Integer.parseInt(masheryApiException.getMasheryError().getCode());

            switch (errorCode) {
                case MASHERY_BAD_CLIENT_ID:
                    return new OAuthUnauthorizedClientException();

                default:
                    return new OAuthServerErrorException(e);

            }
        }
        return new OAuthServerErrorException(e);
    }
}
