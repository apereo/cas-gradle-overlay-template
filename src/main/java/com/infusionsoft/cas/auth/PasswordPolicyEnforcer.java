package com.infusionsoft.cas.auth;

public interface PasswordPolicyEnforcer {
    /**
     * @param userId The unique ID of the user
     * @return Number of days to the expiration date, or -1 if checks pass.
     */
    public long getNumberOfDaysToPasswordExpirationDate(final String userId) throws PasswordPolicyEnforcementException;
}
