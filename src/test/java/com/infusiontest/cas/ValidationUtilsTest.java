package com.infusiontest.cas;

import com.infusionsoft.cas.web.ValidationUtils;
import junit.framework.Assert;
import org.testng.annotations.Test;

/**
 * Test case of some of the string validation/sanitizing functionality.
 */
public class ValidationUtilsTest {
    @Test
    public void testSanitizeAppName() {
        Assert.assertEquals("Normal app name should be unchanged", "myawesomeapp", ValidationUtils.sanitizeAppName("myawesomeapp"));
        Assert.assertEquals("App name with hyphens should be unchanged", "my-awesome-app", ValidationUtils.sanitizeAppName("my-awesome-app"));
        Assert.assertEquals("White space should be removed", "myawesomeapp", ValidationUtils.sanitizeAppName("my awesome app"));
        Assert.assertEquals("Junk characters should be removed", "myawesomeapp", ValidationUtils.sanitizeAppName("my%'\"<>awesome_app.*"));
        Assert.assertEquals("Everything should be lowercase", "myawesomeapp", ValidationUtils.sanitizeAppName("MyAwesomeApp"));
    }

    @Test
    public void testSanitizeAppAlias() {
        Assert.assertEquals("Normal stuff should be unchanged", "My Favorite App", ValidationUtils.sanitizeAppAlias("My Favorite App"));
        Assert.assertEquals("Quotes and spaces are okay", "Andy's \"Fluffy Bunny\" App", ValidationUtils.sanitizeAppAlias("Andy's \"Fluffy Bunny\" App"));
        Assert.assertEquals("Hyphens and underscores are okay", "fluffy_bunny_app-1", ValidationUtils.sanitizeAppAlias("fluffy_bunny_app-1"));
    }
}
