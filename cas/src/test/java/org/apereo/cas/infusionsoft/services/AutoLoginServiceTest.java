package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.CentralAuthenticationService;
import org.apereo.cas.authentication.AuthenticationResult;
import org.apereo.cas.authentication.AuthenticationSystemSupport;
import org.apereo.cas.authentication.DefaultAuthenticationResult;
import org.apereo.cas.infusionsoft.authentication.LetMeInCredentials;
import org.apereo.cas.ticket.TicketGrantingTicket;
import org.apereo.cas.ticket.TicketGrantingTicketImpl;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.support.CookieRetrievingCookieGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AutoLoginServiceTest {

    private static final String TEST_USERNAME = "usernameForTesting";

    private AutoLoginService serviceToTest;

    @Mock
    private CentralAuthenticationService centralAuthenticationService;

    @Mock
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    @Mock
    private TicketRegistry ticketRegistry;

    @Mock
    private AuthenticationSystemSupport authenticationSystemSupport;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private TicketGrantingTicket ticketGrantingTicket;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        serviceToTest = spy(new AutoLoginService(centralAuthenticationService, ticketGrantingTicketCookieGenerator, ticketRegistry, authenticationSystemSupport));
    }

    @Test
    public void testAutoLoginSuccess() throws Exception {
        final String oldTicketGrantingTicketId = "oldTicketGrantingTicketId";
        doReturn(oldTicketGrantingTicketId).when(ticketGrantingTicketCookieGenerator).retrieveCookieValue(request);

        final TicketGrantingTicket ticket = new TicketGrantingTicketImpl();
        doReturn(ticket).when(ticketRegistry).getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);

        final AuthenticationResult authenticationResult = new DefaultAuthenticationResult(null);
        when(authenticationSystemSupport.handleAndFinalizeSingleAuthenticationTransaction(any(), any())).thenReturn(authenticationResult);

        final String ticketGrantingTicketId = "newTicketGrantingTicketId";
        when(ticketGrantingTicket.getId()).thenReturn(ticketGrantingTicketId);
        when(centralAuthenticationService.createTicketGrantingTicket(any(AuthenticationResult.class))).thenReturn(ticketGrantingTicket);

        final boolean loginResult = serviceToTest.autoLogin(TEST_USERNAME, request, response);

        Assert.assertTrue(loginResult);

        verify(serviceToTest, times(1)).killTGT(request);

        ArgumentCaptor<LetMeInCredentials> credentialsArgumentCaptor = ArgumentCaptor.forClass(LetMeInCredentials.class);
        verify(authenticationSystemSupport, times(1)).handleAndFinalizeSingleAuthenticationTransaction(any(), credentialsArgumentCaptor.capture());
        Assert.assertEquals(credentialsArgumentCaptor.getValue().getUsername(), TEST_USERNAME);

        ArgumentCaptor<AuthenticationResult> authResultArgumentCaptor = ArgumentCaptor.forClass(AuthenticationResult.class);
        verify(centralAuthenticationService, times(1)).createTicketGrantingTicket(authResultArgumentCaptor.capture());
        Assert.assertSame(authenticationResult, authResultArgumentCaptor.getValue());

        verify(ticketGrantingTicketCookieGenerator, times(1)).addCookie(request, response, ticketGrantingTicketId);
    }

    @Test
    public void testAutoLoginException() throws Exception {
        final String oldTicketGrantingTicketId = "oldTicketGrantingTicketId";
        doReturn(oldTicketGrantingTicketId).when(ticketGrantingTicketCookieGenerator).retrieveCookieValue(request);

        final TicketGrantingTicket ticket = new TicketGrantingTicketImpl();
        doReturn(ticket).when(ticketRegistry).getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);

        final AuthenticationResult authenticationResult = new DefaultAuthenticationResult(null);
        when(authenticationSystemSupport.handleAndFinalizeSingleAuthenticationTransaction(any(), any())).thenReturn(authenticationResult);

        final String ticketGrantingTicketId = "newTicketGrantingTicketId";
        when(ticketGrantingTicket.getId()).thenReturn(ticketGrantingTicketId);
        when(centralAuthenticationService.createTicketGrantingTicket(any(AuthenticationResult.class))).thenReturn(ticketGrantingTicket);

        doThrow(new RuntimeException()).when(ticketGrantingTicketCookieGenerator).addCookie(request, response, ticketGrantingTicketId);

        final boolean loginResult = serviceToTest.autoLogin(TEST_USERNAME, request, response);

        Assert.assertFalse(loginResult);

        verify(serviceToTest, times(1)).killTGT(request);

        ArgumentCaptor<LetMeInCredentials> credentialsArgumentCaptor = ArgumentCaptor.forClass(LetMeInCredentials.class);
        verify(authenticationSystemSupport, times(1)).handleAndFinalizeSingleAuthenticationTransaction(any(), credentialsArgumentCaptor.capture());
        Assert.assertEquals(credentialsArgumentCaptor.getValue().getUsername(), TEST_USERNAME);

        ArgumentCaptor<AuthenticationResult> authResultArgumentCaptor = ArgumentCaptor.forClass(AuthenticationResult.class);
        verify(centralAuthenticationService, times(1)).createTicketGrantingTicket(authResultArgumentCaptor.capture());
        Assert.assertSame(authenticationResult, authResultArgumentCaptor.getValue());

        verify(ticketGrantingTicketCookieGenerator, times(1)).addCookie(request, response, ticketGrantingTicketId);
    }

    @Test
    public void testKillTGT() {
        final String oldTicketGrantingTicketId = "oldTicketGrantingTicketId";
        doReturn(oldTicketGrantingTicketId).when(ticketGrantingTicketCookieGenerator).retrieveCookieValue(request);

        final TicketGrantingTicket ticket = new TicketGrantingTicketImpl();
        doReturn(ticket).when(ticketRegistry).getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);

        serviceToTest.killTGT(request);

        verify(ticketGrantingTicketCookieGenerator, times(1)).retrieveCookieValue(request);
        verify(ticketRegistry, times(1)).getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);
        verify(centralAuthenticationService, times(1)).destroyTicketGrantingTicket(oldTicketGrantingTicketId);
    }

    @Test
    public void testKillTGTNoCookie() {
        doReturn(null).when(ticketGrantingTicketCookieGenerator).retrieveCookieValue(request);

        serviceToTest.killTGT(request);

        verify(ticketGrantingTicketCookieGenerator, times(1)).retrieveCookieValue(request);
        verifyZeroInteractions(ticketRegistry, centralAuthenticationService);
    }

    @Test
    public void testKillTGTNoTicket() {
        final String oldTicketGrantingTicketId = "oldTicketGrantingTicketId";
        doReturn(oldTicketGrantingTicketId).when(ticketGrantingTicketCookieGenerator).retrieveCookieValue(request);

        doReturn(null).when(ticketRegistry).getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);

        serviceToTest.killTGT(request);

        verify(ticketGrantingTicketCookieGenerator, times(1)).retrieveCookieValue(request);
        verify(ticketRegistry, times(1)).getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);
        verifyZeroInteractions(centralAuthenticationService);
    }

}
