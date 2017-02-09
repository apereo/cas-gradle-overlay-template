package org.apereo.cas.infusionsoft.events;

import org.apereo.cas.infusionsoft.domain.User;
import org.springframework.context.ApplicationEvent;

/**
 * Event for when a user is deleted
 */
public class UserDeletedEvent extends ApplicationEvent {

    public UserDeletedEvent(User source) {
        super(source);
    }

    public User getUser() {
        return (User) this.source;
    }

}
