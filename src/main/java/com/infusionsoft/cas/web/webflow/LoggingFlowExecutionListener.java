package com.infusionsoft.cas.web.webflow;

import org.apache.log4j.Logger;
import org.springframework.binding.mapping.MappingResult;
import org.springframework.webflow.engine.FlowAttributeMappingException;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.RequestContext;

import java.util.List;

/**
 * This is what we have to resort to to make stupid WebFlow log exceptions. Yes, we had to implement a custom class to
 * make it do what any sensible framework would do by default.
 */
public class LoggingFlowExecutionListener extends FlowExecutionListenerAdapter {
    private final Logger logger = Logger.getLogger(LoggingFlowExecutionListener.class);

    @Override
    @SuppressWarnings("unchecked")
    public void exceptionThrown(RequestContext context, FlowExecutionException exception) {
        if (exception instanceof FlowAttributeMappingException) {
            List<MappingResult> errors = ((FlowAttributeMappingException) exception).getMappingResults().getErrorResults();

            for (MappingResult error : errors) {
                if (error.getErrorCause() != null) {
                    logger.warn("FlowAttributeMappingException thown containing error : " + error.getErrorCause(), error.getErrorCause());
                }
            }
        }

        logger.error("ERROR IN STUPID WEBFLOW: " + exception.getCause(), exception.getCause());
    }
}

