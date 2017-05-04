package org.apereo.cas.infusionsoft.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("buildService")
public class BuildServiceImpl {
    @Value("${build.version}")
    String buildVersion;

    public String getBuildVersion(){
        return buildVersion;
    }
}
