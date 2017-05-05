package org.apereo.cas.infusionsoft.services;

import org.apereo.cas.infusionsoft.authentication.LetMeInCredentials;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.TicketGrantingTicketImpl;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AutoLoginServiceTest {

    private static final String TEST_USERNAME = "usernameForTesting";

    @InjectMocks
    private AutoLoginService serviceToTest = spy(new AutoLoginService());

    @Mock
    private TicketRegistry ticketRegistry;

    @Mock
    private CentralAuthenticationService centralAuthenticationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock(name = "ticketGrantingTicketCookieGenerator")
    private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAutoLoginSuccess() throws Exception {
        final String oldTicketGrantingTicketId = "oldTicketGrantingTicketId";
        doReturn(oldTicketGrantingTicketId).when(ticketGrantingTicketCookieGenerator).retrieveCookieValue(request);

        final TicketGrantingTicket ticket = new TicketGrantingTicketImpl();
        doReturn(ticket).when(ticketRegistry).getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);

        final String ticketGrantingTicketId = "newTicketGrantingTicketId";
        doReturn(ticketGrantingTicketId).when(centralAuthenticationService).createTicketGrantingTicket(any(Credentials.class));

        final boolean loginResult = serviceToTest.autoLogin(TEST_USERNAME, request, response);

        Assert.assertTrue(loginResult);

        verify(serviceToTest, times(1)).killTGT(request);

        ArgumentCaptor<Credentials> credentialsArgumentCaptor = ArgumentCaptor.forClass(Credentials.class);
        verify(centralAuthenticationService, times(1)).createTicketGrantingTicket(credentialsArgumentCaptor.capture());

        final LetMeInCredentials credentials = (LetMeInCredentials)credentialsArgumentCaptor.getValue();
        Assert.assertEquals(credentials.getUsername(), TEST_USERNAME);

        verify(ticketGrantingTicketCookieGenerator, times(1)).addCookie(request, response, ticketGrantingTicketId);
    }

    @Test
    public void testAutoLoginException() throws Exception {
        final String oldTicketGrantingTicketId = "oldTicketGrantingTicketId";
        doReturn(oldTicketGrantingTicketId).when(ticketGrantingTicketCookieGenerator).retrieveCookieValue(request);

        final TicketGrantingTicket ticket = new TicketGrantingTicketImpl();
        doReturn(ticket).when(ticketRegistry).getTicket(oldTicketGrantingTicketId, TicketGrantingTicket.class);

        final String ticketGrantingTicketId = "newTicketGrantingTicketId";
        doReturn(ticketGrantingTicketId).when(centralAuthenticationService).createTicketGrantingTicket(any(Credentials.class));

        doThrow(new RuntimeException()).when(ticketGrantingTicketCookieGenerator).addCookie(request, response, ticketGrantingTicketId);

        final boolean loginResult = serviceToTest.autoLogin(TEST_USERNAME, request, response);

        Assert.assertFalse(loginResult);

        verify(serviceToTest, times(1)).killTGT(request);

        ArgumentCaptor<Credentials> credentialsArgumentCaptor = ArgumentCaptor.forClass(Credentials.class);
        verify(centralAuthenticationService, times(1)).createTicketGrantingTicket(credentialsArgumentCaptor.capture());

        final LetMeInCredentials credentials = (LetMeInCredentials)credentialsArgumentCaptor.getValue();
        Assert.assertEquals(credentials.getUsername(), TEST_USERNAME);

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
