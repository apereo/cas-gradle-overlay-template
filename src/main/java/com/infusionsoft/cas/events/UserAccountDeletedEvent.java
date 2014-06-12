package com.infusionsoft.cas.events;

import com.infusionsoft.cas.domain.UserAccount;
import org.springframework.context.ApplicationEvent;

/**
 * Event for when a user account is deleted
 */
public class UserAccountDeletedEvent extends ApplicationEvent {

    public UserAccountDeletedEvent(UserAccount userAccount) {
        super(userAccount);
    }

    public UserAccount getUserAccount() {
        return (UserAccount) source;
    }

}
