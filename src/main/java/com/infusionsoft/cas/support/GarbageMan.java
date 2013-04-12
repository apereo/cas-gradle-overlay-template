package com.infusionsoft.cas.support;

import com.infusionsoft.cas.services.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Simple Quartz job that cleans up old login attempts from the database.
 */
@Component
public class GarbageMan {
    private static final Logger log = Logger.getLogger(GarbageMan.class);

    @Autowired
    UserService userService;

    public void cleanup() {
        log.info("Cleaning up Login Attempts");
        userService.cleanupLoginAttempts();
    }
}
