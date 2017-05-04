package org.apereo.cas.api;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Error object returned from API calls.
  */
@JsonTypeName("APIError")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class APIErrorDTO<T> {
    private String code;
    private String message;
    private T relatedObject;

    public APIErrorDTO(String code, String message) {
        this(code, message, null);
    }

    public APIErrorDTO(String code, MessageSource messageSource, Object[] args, Locale locale) {
        this(code, messageSource.getMessage(code, args, locale), null);
    }

    public APIErrorDTO(String code, MessageSource messageSource, Locale locale) {
        this(code, messageSource.getMessage(code, null, locale), null);
    }

    public APIErrorDTO(String code, MessageSource messageSource, Object[] args, Locale locale, T relatedObject) {
        this(code, messageSource.getMessage(code, args, locale), relatedObject);
    }

    public APIErrorDTO(String code, MessageSource messageSource, Locale locale, T relatedObject) {
        this(code, messageSource.getMessage(code, null, locale), relatedObject);
    }

    @JsonCreator
    public APIErrorDTO(@JsonProperty("code") String code, @JsonProperty("message") String message, @JsonProperty("relatedObject") T relatedObject) {
        this.code = code;
        this.message = message;
        this.relatedObject = relatedObject;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = UserDTO[].class, name = "User[]"),
            @JsonSubTypes.Type(value = UserDTO.class, name = "User"),
            @JsonSubTypes.Type(value = AccountDTO[].class, name = "Account[]"),
            @JsonSubTypes.Type(value = AccountDTO.class, name = "Account"),
            @JsonSubTypes.Type(value = UserAccountDTO[].class, name = "UserAccount[]"),
            @JsonSubTypes.Type(value = UserAccountDTO.class, name = "UserAccount"),
            @JsonSubTypes.Type(value = APIErrorDTO[].class, name = "Error[]"),
            @JsonSubTypes.Type(value = APIErrorDTO.class, name = "Error")})
    public T getRelatedObject() {
        return relatedObject;
    }
}