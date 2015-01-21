package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.domain.User;
import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class TGTAuthenticationFilter extends GenericFilterBean {

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    @Autowired
    @Qualifier("casAuthenticationManager") // TODO: is this the right authentication manager?
    protected AuthenticationManager authenticationManager;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        TicketGrantingTicket ticketGrantingTicket = infusionsoftAuthenticationService.getTicketGrantingTicket(request);
        TGTAuthenticationToken tgtAuthenticationToken = new TGTAuthenticationToken(ticketGrantingTicket);
        tgtAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));

        Authentication authResult = authenticationManager.authenticate(tgtAuthenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authResult);

        chain.doFilter(servletRequest, servletResponse);
    }

}
