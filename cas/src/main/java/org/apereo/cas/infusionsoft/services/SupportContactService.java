package org.apereo.cas.infusionsoft.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SupportContactService {

    @Value("${infusionsoft.cas.support.phone.us}")
    private String supportPhoneNumberUS;

    @Value("${infusionsoft.cas.support.phone.uk}")
    private String supportPhoneNumberUK;

    @Value("${infusionsoft.cas.support.phone.aus}")
    private String supportPhoneNumberAUS;

    public List<String> getSupportPhoneNumbers() {
        return Arrays.asList(supportPhoneNumberUS, supportPhoneNumberUK, supportPhoneNumberAUS);
    }

}
