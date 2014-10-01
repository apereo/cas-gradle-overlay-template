package com.infusionsoft.cas.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.stereotype.Component;

@Component("objectMapper")
public class InfusionsoftObjectMapper extends ObjectMapper {

    public InfusionsoftObjectMapper() {
        super();
        registerModule(new JodaModule());
    }

}
