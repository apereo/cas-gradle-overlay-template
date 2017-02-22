package org.apereo.cas.infusionsoft.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

@Service("buildService")
public class BuildServiceImpl {

    @Autowired
    BuildProperties buildProperties;

    public String getBuildVersion() {
        return buildProperties.getVersion();
    }
}
