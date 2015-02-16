package com.infusionsoft.cas.support;

import org.jasig.cas.util.PrivateKeyFactoryBean;
import org.jasig.cas.util.PublicKeyFactoryBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Simple test for the SAMLv2 argument extractor. Heavily borrowed from the GoogleAccountsArgumentExtractor tests
 * in the CAS source tree.
 */
public class InfusionsoftSaml2ArgumentExtractorTests {
    private InfusionsoftSaml2ArgumentExtractor extractor;

    @Before
    public void setUp() throws Exception {
        final PublicKeyFactoryBean pubKeyFactoryBean = new PublicKeyFactoryBean();
        final PrivateKeyFactoryBean privKeyFactoryBean = new PrivateKeyFactoryBean();

        pubKeyFactoryBean.setAlgorithm("DSA");
        privKeyFactoryBean.setAlgorithm("DSA");

        final ClassPathResource pubKeyResource = new ClassPathResource("DSAPublicKey01.key");
        final ClassPathResource privKeyResource = new ClassPathResource("DSAPrivateKey01.key");

        pubKeyFactoryBean.setLocation(pubKeyResource);
        privKeyFactoryBean.setLocation(privKeyResource);
        Assert.assertTrue(privKeyFactoryBean.getObjectType().equals(PrivateKey.class));
        Assert.assertTrue(pubKeyFactoryBean.getObjectType().equals(PublicKey.class));
        pubKeyFactoryBean.afterPropertiesSet();
        privKeyFactoryBean.afterPropertiesSet();

        this.extractor = new InfusionsoftSaml2ArgumentExtractor();
        this.extractor.setPrivateKey((PrivateKey) privKeyFactoryBean.getObject());
        this.extractor.setPublicKey((PublicKey) pubKeyFactoryBean.getObject());
    }

    @Test
    public void testNoService() {
        Assert.assertNull(this.extractor.extractService(new MockHttpServletRequest()));
    }
}
