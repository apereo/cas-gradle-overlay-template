package com.infusionsoft.cas.events;

import com.infusionsoft.cas.domain.User;
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
