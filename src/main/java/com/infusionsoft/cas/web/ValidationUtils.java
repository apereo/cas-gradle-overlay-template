package com.infusionsoft.cas.web;

/**
 * Simple utility for validating and sanitizing some of our common input strings.
 */
public class ValidationUtils {
    /**
     * Make sure the app name consists only of alphanumeric and hyphen characters.
     */
    public static String sanitizeAppName(String appName) {
        if (appName == null) {
            return null;
        } else {
            return appName.replaceAll("[^a-zA-Z0-9\\-]", "").toLowerCase();
        }
    }

    /**
     * Make sure the app alias only has alphanumeric and common punctuation characters.
     */
    public static String sanitizeAppAlias(String appAlias) {
        if (appAlias == null) {
            return null;
        } else {
            return appAlias.replaceAll("[^\\w\\-'\"\\s]", "");
        }
    }
}
