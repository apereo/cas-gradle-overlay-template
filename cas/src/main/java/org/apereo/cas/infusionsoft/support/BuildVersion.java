package org.apereo.cas.infusionsoft.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BuildVersion {
    private static final Logger log = LoggerFactory.getLogger(BuildVersion.class);
    private static String casVersion;

    private BuildVersion() {
    }

    public static String getBuildVersion() {
        if (casVersion == null) {
            Properties properties = new Properties();
            try {
                InputStream buildFile = BuildVersion.class.getClassLoader().getResourceAsStream("build.properties");
                properties.load(buildFile);
                casVersion = properties.get("build.version").toString();
            } catch (IOException e) {
                log.error("Unable to load build version", e);
            } catch (NullPointerException e) {
                log.error("Unable to load build version", e);
            }
        }
        return casVersion;
    }
}
