//package com.infusionsoft.cas.auth;
//
//import com.infusionsoft.cas.support.InfusionsoftAttributeRepository;
//import org.jasig.cas.authentication.principal.AbstractPersonDirectoryCredentialsToPrincipalResolver;
//import org.jasig.cas.authentication.principal.Credentials;
//import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
//import org.jasig.cas.authentication.principal.UsernamePasswordCredentialsToPrincipalResolver;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * Alternate version of UsernamePasswordCredentialsToPrincipalResolver, wherein we add our custom attributes.
// */
//@Component
//public class InfusionsoftCredentialsToPrincipalResolver extends AbstractPersonDirectoryCredentialsToPrincipalResolver {
//
//    @Autowired
//    InfusionsoftAttributeRepository infusionsoftAttributeRepository;
//
//    public InfusionsoftCredentialsToPrincipalResolver() {
//        this.setAttributeRepository(infusionsoftAttributeRepository);
//    }
//
//    protected String extractPrincipalId(final Credentials credentials) {
//        final UsernamePasswordCredentials usernamePasswordCredentials = (UsernamePasswordCredentials) credentials;
//        return usernamePasswordCredentials.getUsername();
//    }
//
//    public boolean supports(final Credentials credentials) {
//        return credentials != null && UsernamePasswordCredentials.class.isAssignableFrom(credentials.getClass());
//    }
//}
