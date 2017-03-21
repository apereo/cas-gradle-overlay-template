package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.OAuthServiceConfig;
import com.infusionsoft.cas.oauth.exceptions.OAuthInvalidRequestException;
import com.infusionsoft.cas.oauth.exceptions.OAuthServerErrorException;
import com.infusionsoft.cas.oauth.exceptions.OAuthUnsupportedGrantTypeException;
import com.infusionsoft.cas.services.OAuthServiceConfigService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class OAuthAuthenticationFilterTest {

    @InjectMocks
    private OAuthAuthenticationFilter filterToTest = new OAuthAuthenticationFilter();

    @Mock
    private AuthenticationManager oauthAuthenticationManager;

    @Mock
    private OAuthAuthenticationEntryPoint oAuthAuthenticationEntryPoint;

    @Mock
    private OAuthServiceConfigService oAuthServiceConfigService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private OAuthFilterTokenProvider dummyTokenProvider;

    @Mock
    private OAuthAuthenticationToken token;

    @Mock
    private OAuthAuthenticationToken authToken;

    @Spy
    private List<OAuthFilterTokenProvider> tokenProviders = new ArrayList<>();

    @Captor
    private ArgumentCaptor<AuthenticationException> authenticationExceptionArgumentCaptor;

    @Mock
    private FilterChain chain;

    private static final String scope = "scope";
    private static final String application = "application";
    private static final String grantType = "grantType";
    private static final String clientId = "clientId";
    private static final String clientSecret = "clientSecret";
    private static final String serviceName = "serviceName";
    private static final OAuthServiceConfig oAuthServiceConfig = new OAuthServiceConfig();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        tokenProviders.add(dummyTokenProvider);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testDoFilter() throws Exception {
        when(request.getParameter("scope")).thenReturn(scope);
        when(request.getParameter("grant_type")).thenReturn(grantType);
        when(request.getParameter("client_id")).thenReturn(clientId);
        when(request.getParameter("client_secret")).thenReturn(clientSecret);
        when(request.getParameter("service_name")).thenReturn(serviceName);
        when(oAuthServiceConfigService.loadOAuthServiceConfig(serviceName)).thenReturn(oAuthServiceConfig);
        when(dummyTokenProvider.createAuthenticationToken(request, response, scope, "", grantType, oAuthServiceConfig, clientId, clientSecret)).thenReturn(token);
        when(oauthAuthenticationManager.authenticate(token)).thenReturn(authToken);

        filterToTest.doFilter(request, response, chain);

        verify(dummyTokenProvider, times(1)).createAuthenticationToken(request, response, scope, "", grantType, oAuthServiceConfig, clientId, clientSecret);
        verify(oauthAuthenticationManager, times(1)).authenticate(token);
        Assert.assertEquals(authToken, SecurityContextHolder.getContext().getAuthentication());
        verify(oAuthAuthenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    public void testDoFilterCrm() throws Exception {
        final String serviceName = "crm";
        when(request.getParameter("scope")).thenReturn(scope + "|" + application);
        when(request.getParameter("grant_type")).thenReturn(grantType);
        when(request.getParameter("client_id")).thenReturn(clientId);
        when(request.getParameter("client_secret")).thenReturn(clientSecret);
        when(request.getParameter("service_name")).thenReturn(serviceName);
        when(oAuthServiceConfigService.loadOAuthServiceConfig(serviceName)).thenReturn(oAuthServiceConfig);
        when(dummyTokenProvider.createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret)).thenReturn(token);
        when(oauthAuthenticationManager.authenticate(token)).thenReturn(authToken);

        filterToTest.doFilter(request, response, chain);

        verify(dummyTokenProvider, times(1)).createAuthenticationToken(request, response, scope, application, grantType, oAuthServiceConfig, clientId, clientSecret);
        verify(oauthAuthenticationManager, times(1)).authenticate(token);
        Assert.assertEquals(authToken, SecurityContextHolder.getContext().getAuthentication());
        verify(oAuthAuthenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    public void testDoFilterCrmNoScope() throws Exception {
        final String serviceName = "crm";
        when(request.getParameter("scope")).thenReturn(application);
        when(request.getParameter("grant_type")).thenReturn(grantType);
        when(request.getParameter("client_id")).thenReturn(clientId);
        when(request.getParameter("client_secret")).thenReturn(clientSecret);
        when(request.getParameter("service_name")).thenReturn(serviceName);
        when(oAuthServiceConfigService.loadOAuthServiceConfig(serviceName)).thenReturn(oAuthServiceConfig);
        when(dummyTokenProvider.createAuthenticationToken(request, response, "", application, grantType, oAuthServiceConfig, clientId, clientSecret)).thenReturn(token);
        when(oauthAuthenticationManager.authenticate(token)).thenReturn(authToken);

        filterToTest.doFilter(request, response, chain);

        verify(dummyTokenProvider, times(1)).createAuthenticationToken(request, response, "", application, grantType, oAuthServiceConfig, clientId, clientSecret);
        verify(oauthAuthenticationManager, times(1)).authenticate(token);
        Assert.assertEquals(authToken, SecurityContextHolder.getContext().getAuthentication());
        verify(oAuthAuthenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    public void testDoFilterBasicAuth() throws Exception {
        when(request.getParameter("scope")).thenReturn(scope);
        when(request.getParameter("grant_type")).thenReturn(grantType);
        when(request.getParameter("client_id")).thenReturn("bugus");
        when(request.getParameter("client_secret")).thenReturn("bugus");
        when(request.getParameter("service_name")).thenReturn(serviceName);
        when(request.getHeader("Authorization")).thenReturn("Basic " + new String(Base64.encode((clientId + ":" + clientSecret).getBytes("UTF-8")), "UTF-8"));
        when(oAuthServiceConfigService.loadOAuthServiceConfig(serviceName)).thenReturn(oAuthServiceConfig);
        when(dummyTokenProvider.createAuthenticationToken(request, response, scope, "", grantType, oAuthServiceConfig, clientId, clientSecret)).thenReturn(token);
        when(oauthAuthenticationManager.authenticate(token)).thenReturn(authToken);

        filterToTest.doFilter(request, response, chain);

        verify(dummyTokenProvider, times(1)).createAuthenticationToken(request, response, scope, "", grantType, oAuthServiceConfig, clientId, clientSecret);
        verify(oauthAuthenticationManager, times(1)).authenticate(token);
        Assert.assertEquals(authToken, SecurityContextHolder.getContext().getAuthentication());
        verify(oAuthAuthenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    public void testDoFilterBasicAuthBadBase64() throws Exception {
        when(request.getParameter("scope")).thenReturn(scope);
        when(request.getParameter("grant_type")).thenReturn(grantType);
        when(request.getParameter("client_id")).thenReturn("bugus");
        when(request.getParameter("client_secret")).thenReturn("bugus");
        when(request.getParameter("service_name")).thenReturn(serviceName);
        when(request.getHeader("Authorization")).thenReturn("Basic zz");
        when(oAuthServiceConfigService.loadOAuthServiceConfig(serviceName)).thenReturn(oAuthServiceConfig);
        when(dummyTokenProvider.createAuthenticationToken(request, response, scope, "", grantType, oAuthServiceConfig, clientId, clientSecret)).thenReturn(token);
        when(oauthAuthenticationManager.authenticate(token)).thenReturn(authToken);

        filterToTest.doFilter(request, response, chain);

        verify(dummyTokenProvider, never()).createAuthenticationToken(any(), any(), any(), any(), any(), any(), any(), any());
        verify(oauthAuthenticationManager, never()).authenticate(any());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(oAuthAuthenticationEntryPoint, times(1)).commence(any(), any(), authenticationExceptionArgumentCaptor.capture());
        final AuthenticationException authenticationException = authenticationExceptionArgumentCaptor.getValue();
        Assert.assertEquals(OAuthInvalidRequestException.class, authenticationException.getClass());
        Assert.assertEquals("oauth.exception.clientId.bad", ((OAuthInvalidRequestException) authenticationException).getErrorDescription());
    }

    @Test
    public void testDoFilterBasicAuthNoDelimeter() throws Exception {
        when(request.getParameter("scope")).thenReturn(scope);
        when(request.getParameter("grant_type")).thenReturn(grantType);
        when(request.getParameter("client_id")).thenReturn("bugus");
        when(request.getParameter("client_secret")).thenReturn("bugus");
        when(request.getParameter("service_name")).thenReturn(serviceName);
        when(request.getHeader("Authorization")).thenReturn("Basic " + new String(Base64.encode((clientId + clientSecret).getBytes("UTF-8")), "UTF-8"));
        when(oAuthServiceConfigService.loadOAuthServiceConfig(serviceName)).thenReturn(oAuthServiceConfig);
        when(dummyTokenProvider.createAuthenticationToken(request, response, scope, "", grantType, oAuthServiceConfig, clientId, clientSecret)).thenReturn(token);
        when(oauthAuthenticationManager.authenticate(token)).thenReturn(authToken);

        filterToTest.doFilter(request, response, chain);

        verify(dummyTokenProvider, never()).createAuthenticationToken(any(), any(), any(), any(), any(), any(), any(), any());
        verify(oauthAuthenticationManager, never()).authenticate(any());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(oAuthAuthenticationEntryPoint, times(1)).commence(any(), any(), authenticationExceptionArgumentCaptor.capture());
        final AuthenticationException authenticationException = authenticationExceptionArgumentCaptor.getValue();
        Assert.assertEquals(OAuthInvalidRequestException.class, authenticationException.getClass());
        Assert.assertEquals("oauth.exception.clientSecret.bad", ((OAuthInvalidRequestException) authenticationException).getErrorDescription());
    }

    @Test
    public void testDoFilterNoGrantType() throws Exception {
        tokenProviders.clear();
        filterToTest.doFilter(request, response, chain);

        verify(dummyTokenProvider, never()).createAuthenticationToken(any(), any(), any(), any(), any(), any(), any(), any());
        verify(oauthAuthenticationManager, never()).authenticate(any());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(oAuthAuthenticationEntryPoint, times(1)).commence(any(), any(), authenticationExceptionArgumentCaptor.capture());
        final AuthenticationException authenticationException = authenticationExceptionArgumentCaptor.getValue();
        Assert.assertEquals(OAuthInvalidRequestException.class, authenticationException.getClass());
        Assert.assertEquals("oauth.exception.grantType.missing", ((OAuthInvalidRequestException) authenticationException).getErrorDescription());
    }

    @Test
    public void testDoFilterNoProvider() throws Exception {
        tokenProviders.clear();
        when(request.getParameter("grant_type")).thenReturn("grantType");

        filterToTest.doFilter(request, response, chain);

        verify(dummyTokenProvider, never()).createAuthenticationToken(any(), any(), any(), any(), any(), any(), any(), any());
        verify(oauthAuthenticationManager, never()).authenticate(any());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(oAuthAuthenticationEntryPoint, times(1)).commence(any(), any(), authenticationExceptionArgumentCaptor.capture());
        final AuthenticationException authenticationException = authenticationExceptionArgumentCaptor.getValue();
        Assert.assertEquals(OAuthUnsupportedGrantTypeException.class, authenticationException.getClass());
        Assert.assertEquals("oauth.exception.unsupported.grant.type", ((OAuthUnsupportedGrantTypeException) authenticationException).getErrorDescription());
    }

    @Test
    public void testDoFilterUnknownException() throws Exception {
        when(request.getParameter("grant_type")).thenReturn(grantType);
        when(oAuthServiceConfigService.loadOAuthServiceConfig(serviceName)).thenReturn(oAuthServiceConfig);
        doThrow(Exception.class).when(dummyTokenProvider).createAuthenticationToken(any(), any(), any(), any(), any(), any(), any(), any());
        when(oauthAuthenticationManager.authenticate(token)).thenReturn(authToken);

        filterToTest.doFilter(request, response, chain);

        verify(dummyTokenProvider, times(1)).createAuthenticationToken(any(), any(), any(), any(), any(), any(), any(), any());
        verify(oauthAuthenticationManager, never()).authenticate(any());
        Assert.assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(oAuthAuthenticationEntryPoint, times(1)).commence(any(), any(), authenticationExceptionArgumentCaptor.capture());
        final AuthenticationException authenticationException = authenticationExceptionArgumentCaptor.getValue();
        Assert.assertEquals(OAuthServerErrorException.class, authenticationException.getClass());
        Assert.assertEquals("oauth.exception.server.error", ((OAuthServerErrorException) authenticationException).getErrorDescription());
    }

}
