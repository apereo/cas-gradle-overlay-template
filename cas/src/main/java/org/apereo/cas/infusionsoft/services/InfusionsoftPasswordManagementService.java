package org.apereo.cas.infusionsoft.services;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.CipherExecutor;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.infusionsoft.domain.User;
import org.apereo.cas.infusionsoft.exceptions.InfusionsoftValidationException;
import org.apereo.cas.pm.PasswordChangeBean;
import org.apereo.cas.pm.PasswordManagementService;
import org.apereo.inspektr.common.web.ClientInfo;
import org.apereo.inspektr.common.web.ClientInfoHolder;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component("passwordChangeService")
@EnableConfigurationProperties(CasConfigurationProperties.class)
public class InfusionsoftPasswordManagementService implements PasswordManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfusionsoftPasswordManagementService.class);

    @Autowired
    CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("passwordManagementCipherExecutor")
    CipherExecutor<String, String> cipherExecutor;

    @Autowired
    PasswordService passwordService;

    @Autowired
    SecurityQuestionResponseDAO securityQuestionResponseDAO;

    @Autowired
    UserService userService;

    @Override
    public boolean change(Credential c, PasswordChangeBean bean) {
        boolean retVal = false;

        UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) c;
        User user = userService.loadUser(usernamePasswordCredential.getUsername());

        try {
            passwordService.setPasswordForUser(user, bean.getPassword());
            retVal = true;
        } catch (InfusionsoftValidationException e) {
            LOGGER.error("Unable to validate users password", e);
        }

        return retVal;
    }

    @Override
    public String findEmail(String username) {
        return username;
    }

    @Override
    public String createToken(final String to) {
        try {
            final String token = UUID.randomUUID().toString();
            final JwtClaims claims = new JwtClaims();
            claims.setJwtId(token);
            claims.setIssuer(casProperties.getServer().getPrefix());
            claims.setAudience(casProperties.getServer().getPrefix());
            claims.setExpirationTimeMinutesInTheFuture(casProperties.getAuthn().getPm().getReset().getExpirationMinutes());
            claims.setIssuedAtToNow();

            final ClientInfo holder = ClientInfoHolder.getClientInfo();
            claims.setStringClaim("origin", holder.getServerIpAddress());
            claims.setStringClaim("client", holder.getClientIpAddress());

            claims.setSubject(to);
            final String json = claims.toJson();
            return this.cipherExecutor.encode(json);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String parseToken(final String token) {
        try {
            final String json = this.cipherExecutor.decode(token);
            final JwtClaims claims = JwtClaims.parse(json);

            if (!claims.getIssuer().equals(casProperties.getServer().getPrefix())) {
                LOGGER.error("Token issuer does not match CAS");
                return null;
            }
            if (claims.getAudience().isEmpty() || !claims.getAudience().get(0).equals(casProperties.getServer().getPrefix())) {
                LOGGER.error("Token audience does not match CAS");
                return null;
            }
            if (StringUtils.isBlank(claims.getSubject())) {
                LOGGER.error("Token has no subject identifier");
                return null;
            }

            final ClientInfo holder = ClientInfoHolder.getClientInfo();
            if (!claims.getStringClaimValue("origin").equals(holder.getServerIpAddress())) {
                LOGGER.error("Token origin does not match CAS");
                return null;
            }
            if (!claims.getStringClaimValue("client").equals(holder.getClientIpAddress())) {
                LOGGER.error("Token client does not match CAS");
                return null;
            }

            if (claims.getExpirationTime().isBefore(NumericDate.now())) {
                LOGGER.error("Token has expired.");
                return null;
            }

            return claims.getSubject();
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Map<String, String> getSecurityQuestions(String username) {
        User user = userService.loadUser(username);
        Map<String, String> retVal = new HashMap<>();

        securityQuestionResponseDAO.findAllByUser(user).forEach(securityQuestionResponse ->
                retVal.put(securityQuestionResponse.getSecurityQuestion().getQuestion(), securityQuestionResponse.getResponse())
        );

        return retVal;
    }
}
