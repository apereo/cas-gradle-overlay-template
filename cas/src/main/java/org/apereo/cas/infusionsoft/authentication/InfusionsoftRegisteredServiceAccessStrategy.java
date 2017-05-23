package org.apereo.cas.infusionsoft.authentication;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apereo.cas.services.DefaultRegisteredServiceAccessStrategy;

public class InfusionsoftRegisteredServiceAccessStrategy extends DefaultRegisteredServiceAccessStrategy {
    private static final long serialVersionUID = -6180748828025837049L;

    private boolean forcePasswordExpiration;

    private boolean allowSocialLogin;

    /**
     * Initiates the custom access strategy.
     *
     * @param enabled    is service access allowed?
     * @param ssoEnabled is service allowed to take part in SSO?
     */
    public InfusionsoftRegisteredServiceAccessStrategy(final boolean enabled, final boolean ssoEnabled, final boolean forcePasswordExpiration, final boolean allowSocialLogin) {
        super(enabled, ssoEnabled);
        this.forcePasswordExpiration = forcePasswordExpiration;
        this.allowSocialLogin = allowSocialLogin;
    }

    public boolean isForcePasswordExpiration() {
        return forcePasswordExpiration;
    }

    public boolean isAllowSocialLogin() {
        return allowSocialLogin;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final InfusionsoftRegisteredServiceAccessStrategy rhs = (InfusionsoftRegisteredServiceAccessStrategy) obj;

        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(this.forcePasswordExpiration, rhs.forcePasswordExpiration)
                .append(this.allowSocialLogin, rhs.allowSocialLogin)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.forcePasswordExpiration)
                .append(this.allowSocialLogin)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("forcePasswordExpiration", this.forcePasswordExpiration)
                .append("allowSocialLogin", this.allowSocialLogin)
                .toString();
    }

}
