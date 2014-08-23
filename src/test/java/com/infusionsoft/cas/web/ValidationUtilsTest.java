package com.infusionsoft.cas.web;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test case of some of the string validation/sanitizing functionality.
 */
public class ValidationUtilsTest {
    @Test
    public void testSanitizeAppName() {
        Assert.assertEquals(ValidationUtils.sanitizeAppName("myawesomeapp"), "myawesomeapp", "Normal app name should be unchanged");
        Assert.assertEquals(ValidationUtils.sanitizeAppName("my-awesome-app"), "my-awesome-app", "App name with hyphens should be unchanged");
        Assert.assertEquals(ValidationUtils.sanitizeAppName("my awesome app"), "myawesomeapp", "White space should be removed");
        Assert.assertEquals(ValidationUtils.sanitizeAppName("my%'\"<>awesome_app.*"), "myawesomeapp", "Junk characters should be removed");
        Assert.assertEquals(ValidationUtils.sanitizeAppName("MyAwesomeApp"), "myawesomeapp", "Everything should be lowercase");
    }

    @Test
    public void testSanitizeAppAlias() {
        Assert.assertEquals(ValidationUtils.sanitizeAppAlias("My Favorite App"), "My Favorite App", "Normal stuff should be unchanged");
        Assert.assertEquals(ValidationUtils.sanitizeAppAlias("Andy's \"Fluffy Bunny\" App"), "Andy's \"Fluffy Bunny\" App", "Quotes and spaces are okay");
        Assert.assertEquals(ValidationUtils.sanitizeAppAlias("fluffy_bunny_app-1"), "fluffy_bunny_app-1", "Hyphens and underscores are okay");
    }
}
