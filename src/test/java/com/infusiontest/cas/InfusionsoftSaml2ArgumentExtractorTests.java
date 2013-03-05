package com.infusiontest.cas;

import com.infusionsoft.cas.web.InfusionsoftSaml2ArgumentExtractor;
import junit.framework.TestCase;
import org.jasig.cas.util.PrivateKeyFactoryBean;
import org.jasig.cas.util.PublicKeyFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Simple test for the SAMLv2 argument extractor.
 */
public class InfusionsoftSaml2ArgumentExtractorTests extends TestCase {

    private InfusionsoftSaml2ArgumentExtractor extractor;

    protected void setUp() throws Exception {
        final PublicKeyFactoryBean pubKeyFactoryBean = new PublicKeyFactoryBean();
        final PrivateKeyFactoryBean privKeyFactoryBean = new PrivateKeyFactoryBean();

        pubKeyFactoryBean.setAlgorithm("DSA");
        privKeyFactoryBean.setAlgorithm("DSA");

        final ClassPathResource pubKeyResource = new ClassPathResource("DSAPublicKey01.key");
        final ClassPathResource privKeyResource = new ClassPathResource("DSAPrivateKey01.key");

        pubKeyFactoryBean.setLocation(pubKeyResource);
        privKeyFactoryBean.setLocation(privKeyResource);
        assertTrue(privKeyFactoryBean.getObjectType().equals(PrivateKey.class));
        assertTrue(pubKeyFactoryBean.getObjectType().equals(PublicKey.class));
        pubKeyFactoryBean.afterPropertiesSet();
        privKeyFactoryBean.afterPropertiesSet();

        this.extractor = new InfusionsoftSaml2ArgumentExtractor();
        this.extractor.setPrivateKey((PrivateKey) privKeyFactoryBean.getObject());
        this.extractor.setPublicKey((PublicKey) pubKeyFactoryBean.getObject());

        super.setUp();
    }

    public void testNoService() {
        assertNull(this.extractor.extractService(new MockHttpServletRequest()));
    }
}
