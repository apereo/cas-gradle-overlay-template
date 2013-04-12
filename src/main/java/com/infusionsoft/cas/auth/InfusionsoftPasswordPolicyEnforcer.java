package com.infusionsoft.cas.auth;

import com.infusionsoft.cas.services.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InfusionsoftPasswordPolicyEnforcer implements PasswordPolicyEnforcer {

    @Autowired
    PasswordService passwordService;

    @Value("${infusionsoft.authentication.password.warning.days}")
    Integer warningDays;

    @Override
    public long getNumberOfDaysToPasswordExpirationDate(String userId) throws PasswordPolicyEnforcementException {
        Integer expirationDays = passwordService.getNumberOfDaysToPasswordExpirationDate(userId);

        return expirationDays <= warningDays ? expirationDays : -1;
    }
}
