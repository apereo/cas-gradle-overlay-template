package com.infusionsoft.cas.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

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

    /**
     * Make sure the message code only has alphanumeric characters and periods.
     */
    public static String sanitizeMessageCode(String messageCode) {
        if (messageCode == null) {
            return null;
        } else {
            return messageCode.replaceAll("[^\\w\\.]", "");
        }
    }

    /**
     * Remove all HTML tags from the given string
     */
    public static String removeAllHtmlTags(String unsafe) {
        if (unsafe == null) {
            return null;
        } else {
            // Based on Jsoup.clean; the only difference is text() instead of html()
            Document dirty = Jsoup.parseBodyFragment(unsafe);
            Cleaner cleaner = new Cleaner(Whitelist.none());
            Document clean = cleaner.clean(dirty);
            return clean.body().text();
        }
    }

}
