package com.infusionsoft.cas.services;

public class InfusionsoftValidationException extends Exception {
    private String errorMessageCode;

    public String getErrorMessageCode() {
        return errorMessageCode;
    }

    public void setErrorMessageCode(String errorMessageCode) {
        this.errorMessageCode = errorMessageCode;
    }
}
