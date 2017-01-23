package com.infusionsoft.cas.web.csrf;

import com.infusionsoft.cas.services.InfusionsoftAuthenticationService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper class to get CSRF tokens for a session/request.
 */
@Component
public class CsrfTokenManager {
    private static final Logger log = Logger.getLogger(CsrfTokenManager.class);

    @Value("${csrf.secret}")
    private String secret;

    @Autowired
    private InfusionsoftAuthenticationService infusionsoftAuthenticationService;

    /**
     * Generates a CSRF token using a one-way SHA-256 hash of the user's ticket granting ticket and a server
     * secret. All forms that are submitted by POST are expected to have a parameter matching the token.
     *
     * @param request request
     * @return expected token
     */
    public String getExpectedCsrfTokenForRequest(HttpServletRequest request) {
        try {
            TicketGrantingTicket tgt = infusionsoftAuthenticationService.getTicketGrantingTicket(request);

            if (tgt != null) {
                String tgtId = tgt.getId();
                String raw = secret + tgtId;

                return DigestUtils.sha256Hex(raw);
            }
        } catch (Exception e) {
            log.error("unable to generate token for request", e);
        }

        return null;
    }
}
