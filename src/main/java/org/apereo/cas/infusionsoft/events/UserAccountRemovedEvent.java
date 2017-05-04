package org.apereo.cas.infusionsoft.events;

import org.apereo.cas.infusionsoft.domain.UserAccount;
import org.springframework.context.ApplicationEvent;

/**
 * Event for when a user account is deleted
 */
public class UserAccountRemovedEvent extends ApplicationEvent {

    public UserAccountRemovedEvent(UserAccount userAccount) {
        super(userAccount);
    }

    public UserAccount getUserAccount() {
        return (UserAccount) source;
    }

}
