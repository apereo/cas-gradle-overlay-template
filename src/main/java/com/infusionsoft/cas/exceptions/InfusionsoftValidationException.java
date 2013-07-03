package com.infusionsoft.cas.exceptions;

public class InfusionsoftValidationException extends Exception {
    private final String errorMessageCode;

    public InfusionsoftValidationException(String errorMessageCode) {
        this.errorMessageCode = errorMessageCode;
    }

    public String getErrorMessageCode() {
        return errorMessageCode;
    }
}
