package com.infusiontest.cas;

import com.infusionsoft.cas.support.InfusionsoftSaml2Service;
import junit.framework.TestCase;
import org.apache.commons.codec.binary.Base64;
import org.jasig.cas.authentication.principal.SimplePrincipal;
import org.jasig.cas.util.PrivateKeyFactoryBean;
import org.jasig.cas.util.PublicKeyFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.util.zip.DeflaterOutputStream;

/**
 * Test case for the SAMLv2 service. Ripped off the CAS one for GoogleAccountService.
 */
public class InfusionsoftSaml2ServiceTests extends TestCase {
    private InfusionsoftSaml2Service infusionsoftSaml2Service;

    public static InfusionsoftSaml2Service getInfusionsoftSaml2Service() throws Exception {
        final PublicKeyFactoryBean pubKeyFactoryBean = new PublicKeyFactoryBean();
        pubKeyFactoryBean.setAlgorithm("DSA");
        final PrivateKeyFactoryBean privKeyFactoryBean = new PrivateKeyFactoryBean();
        privKeyFactoryBean.setAlgorithm("DSA");

        final ClassPathResource pubKeyResource = new ClassPathResource("DSAPublicKey01.key");
        final ClassPathResource privKeyResource = new ClassPathResource("DSAPrivateKey01.key");

        pubKeyFactoryBean.setLocation(pubKeyResource);
        privKeyFactoryBean.setLocation(privKeyResource);
        pubKeyFactoryBean.afterPropertiesSet();
        privKeyFactoryBean.afterPropertiesSet();

        final DSAPrivateKey privateKey = (DSAPrivateKey) privKeyFactoryBean.getObject();
        final DSAPublicKey publicKey = (DSAPublicKey) pubKeyFactoryBean.getObject();

        final MockHttpServletRequest request = new MockHttpServletRequest();

        final String SAMLRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><samlp:AuthnRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" ID=\"5545454455\" Version=\"2.0\" IssueInstant=\"Value\" ProtocolBinding=\"urn:oasis:names.tc:SAML:2.0:bindings:HTTP-Redirect\" ProviderName=\"https://localhost:8443/myRutgers\" AssertionConsumerServiceURL=\"https://localhost:8443/myRutgers\"/>";
        request.setParameter("SAMLRequest", encodeMessage(SAMLRequest));

        return InfusionsoftSaml2Service.createServiceFrom(request, privateKey, publicKey, "username");
    }

    protected void setUp() throws Exception {
        this.infusionsoftSaml2Service = getInfusionsoftSaml2Service();
        this.infusionsoftSaml2Service.setPrincipal(new SimplePrincipal("user"));
    }


    // XXX: re-enable when we figure out JVM requirements
    public void testResponse() {
        return;
        //    final Response response = this.infusionsoftSaml2Service.getResponse("ticketId");
        //  assertEquals(ResponseType.POST, response.getResponseType());
        //    assertTrue(response.getAttributes().containsKey("SAMLResponse"));
    }


    protected static String encodeMessage(final String xmlString) throws IOException {
        byte[] xmlBytes = xmlString.getBytes("UTF-8");
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(
                byteOutputStream);
        deflaterOutputStream.write(xmlBytes, 0, xmlBytes.length);
        deflaterOutputStream.close();

        // next, base64 encode it
        Base64 base64Encoder = new Base64();
        byte[] base64EncodedByteArray = base64Encoder.encode(byteOutputStream
                .toByteArray());
        return new String(base64EncodedByteArray);
    }
}
