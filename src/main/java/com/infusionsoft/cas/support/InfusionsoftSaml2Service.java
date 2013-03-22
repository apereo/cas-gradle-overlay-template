package com.infusionsoft.cas.support;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jasig.cas.authentication.principal.AbstractWebApplicationService;
import org.jasig.cas.authentication.principal.Response;
import org.jasig.cas.util.SamlUtils;
import org.jdom.Document;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Our own version of Scott Battaglia's famous Google Accounts Service, wherein we add a few extra SAML claims needed
 * by Mashery (FirstName, LastName, Email).
 * <p/>
 * This guy Scott Battaglia loves to make everything "final" so it can't be extended in a meaningful way. Most of the
 * important methods are private so we can't use composition either. Therefore we pretty much pasted the code in here
 * and changed it to meet our needs. Someday we could build a much better implementation if we have nothing better
 * to do.
 * <p/>
 * TODO: this code should be taken out back and fed to Mr. Wu's pigs
 */
public class InfusionsoftSaml2Service extends AbstractWebApplicationService {
    private static final Logger log = Logger.getLogger(InfusionsoftSaml2Service.class);

    private static final long serialVersionUID = 6678711809842282833L;

    private static Random random = new Random();

    private static final char[] charMapping = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p'};

    private static final String CONST_PARAM_SERVICE = "SAMLRequest";

    private static final String CONST_RELAY_STATE = "RelayState";

    // TODO - we lifted this from the CAS Google Accounts implementation, but it is a very stupid way to make XML
    private static final String TEMPLATE_SAML_RESPONSE = "<?xml version=\"1.0\"?>" +
            "<samlp:Response ID=\"<RESPONSE_ID>\" IssueInstant=\"<ISSUE_INSTANT>\" Version=\"2.0\" xmlns=\"urn:oasis:names:tc:SAML:2.0:assertion\" xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:xenc=\"http://www.w3.org/2001/04/xmlenc#\">" +
            "  <Issuer><ISSUER_STRING></Issuer>" +
            "  <samlp:Status>" +
            "    <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\" />" +
            "  </samlp:Status>" +
            "  <Assertion ID=\"<ASSERTION_ID>\" IssueInstant=\"2003-04-17T00:46:02Z\" Version=\"2.0\" xmlns=\"urn:oasis:names:tc:SAML:2.0:assertion\">" +
            "    <Issuer><ISSUER_STRING></Issuer>" +
            "    <Subject>" +
            "      <NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:emailAddress\">" +
            "        <USERNAME_STRING>" +
            "      </NameID>" +
            "      <SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\">" +
            "        <SubjectConfirmationData Recipient=\"<ACS_URL>\" NotOnOrAfter=\"<NOT_ON_OR_AFTER>\" InResponseTo=\"<REQUEST_ID>\" />" +
            "      </SubjectConfirmation>" +
            "    </Subject>" +
            "    <Conditions NotBefore=\"2003-04-17T00:46:02Z\" NotOnOrAfter=\"<NOT_ON_OR_AFTER>\">" +
            "      <AudienceRestriction>" +
            "        <Audience><ACS_URL></Audience>" +
            "      </AudienceRestriction>" +
            "    </Conditions>" +
            "    <AuthnStatement AuthnInstant=\"<AUTHN_INSTANT>\">" +
            "      <AuthnContext>" +
            "        <AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password</AuthnContextClassRef>" +
            "      </AuthnContext>" +
            "    </AuthnStatement>" +
            "    <AttributeStatement>" +
            "      <ATTRIBUTES>" +
            "    </AttributeStatement>" +
            "  </Assertion>" +
            "</samlp:Response>";

    private final String relayState;

    private final PublicKey publicKey;

    private final PrivateKey privateKey;

    private final String requestId;

    private final String alternateUserName;

    private String issuer;

    private boolean gzipEnabled = false;

    private boolean signAssertionOnly = true;

    protected InfusionsoftSaml2Service(final String id, final String relayState, final String requestId, final PrivateKey privateKey, final PublicKey publicKey, final String alternateUserName) {
        this(id, id, null, relayState, requestId, privateKey, publicKey, alternateUserName);
    }

    protected InfusionsoftSaml2Service(final String id, final String originalUrl, final String artifactId, final String relayState, final String requestId, final PrivateKey privateKey, final PublicKey publicKey, final String alternateUserName) {
        super(id, originalUrl, artifactId, null);

        this.relayState = relayState;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.requestId = requestId;
        this.alternateUserName = alternateUserName;
    }

    public static InfusionsoftSaml2Service createServiceFrom(final HttpServletRequest request, final PrivateKey privateKey, final PublicKey publicKey, final String alternateUserName) {
        try {
            final String relayState = request.getParameter(CONST_RELAY_STATE);
            final String xmlRequest = decodeAuthnRequestXML(request.getParameter(CONST_PARAM_SERVICE));

            if (!StringUtils.hasText(xmlRequest)) {
                return null;
            }

            final Document document = SamlUtils.constructDocumentFromXmlString(xmlRequest);

            if (document == null) {
                log.warn("unable to construct XML document from SAML request");

                return null;
            }

            final String assertionConsumerServiceUrl = document.getRootElement().getAttributeValue("AssertionConsumerServiceURL");
            final String requestId = document.getRootElement().getAttributeValue("ID");

            InfusionsoftSaml2Service service = new InfusionsoftSaml2Service(assertionConsumerServiceUrl, relayState, requestId, privateKey, publicKey, alternateUserName);

            log.debug("created SAMLv2 service");

            return service;
        } catch (Exception e) {
            log.error("failed to create SAMLv2 service", e);
        }

        return null;
    }

    public Response getResponse(final String ticketId) {
        log.debug("creating SAMLv2 response");

        final Map<String, String> parameters = new HashMap<String, String>();

        try {
            String samlResponse = constructSamlResponse();
            String signedResponse;
            String base64Response;

            if (signAssertionOnly) {
                log.debug("signing SAML assertion");

                signedResponse = SamlHelper.signAssertion(samlResponse, this.privateKey, this.publicKey);
            } else {
                log.debug("signing SAML response");

                signedResponse = SamlUtils.signSamlResponse(samlResponse, this.privateKey, this.publicKey);
            }

            if (gzipEnabled) {
                log.debug("Base64 encoding gzipped SAML response");

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                GZIPOutputStream gzip = new GZIPOutputStream(output);

                gzip.write(signedResponse.getBytes("UTF-8"));
                gzip.close();
                output.close();

                base64Response = Base64.encodeBase64String(output.toByteArray());
            } else {
                log.debug("Base64 encoding SAML response");

                base64Response = Base64.encodeBase64String(signedResponse.getBytes("UTF-8"));
            }

            log.debug("SAMLResponse (raw): " + signedResponse);
            log.debug("SAMLResponse (Base64): " + base64Response);
            log.debug("RelayState: " + relayState);

            parameters.put("SAMLResponse", base64Response);
            parameters.put("RelayState", this.relayState);
        } catch (Exception e) {
            log.error("failed to construct SAMLv2 response", e);
        }

        return Response.getPostResponse(getOriginalUrl(), parameters);
    }

    /**
     * Service does not support Single Log Out
     *
     * @see org.jasig.cas.authentication.principal.WebApplicationService#logOutOfService(java.lang.String)
     */
    public boolean logOutOfService(final String sessionIdentifier) {
        return false;
    }

    private String constructSamlResponse() {
        String samlResponse = TEMPLATE_SAML_RESPONSE;

        final Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, 1);

        final String userId;

        if (this.alternateUserName == null) {
            userId = getPrincipal().getId();
        } else {
            final String attributeValue = (String) getPrincipal().getAttributes().get(this.alternateUserName);

            if (attributeValue == null) {
                userId = getPrincipal().getId();
            } else {
                userId = attributeValue;
            }
        }

        samlResponse = samlResponse.replaceAll("<USERNAME_STRING>", userId);
        samlResponse = samlResponse.replaceAll("<RESPONSE_ID>", createID());
        samlResponse = samlResponse.replaceAll("<ISSUER_STRING>", issuer);
        samlResponse = samlResponse.replaceAll("<ISSUE_INSTANT>", SamlUtils.getCurrentDateAndTime());
        samlResponse = samlResponse.replaceAll("<AUTHN_INSTANT>", SamlUtils.getCurrentDateAndTime());
        samlResponse = samlResponse.replaceAll("<NOT_ON_OR_AFTER>", SamlUtils.getFormattedDateAndTime(c.getTime()));
        samlResponse = samlResponse.replaceAll("<ASSERTION_ID>", createID());
        samlResponse = samlResponse.replaceAll("<ACS_URL>", getId());
        samlResponse = samlResponse.replaceAll("<REQUEST_ID>", this.requestId);

        Map<String, Object> attributes = getPrincipal().getAttributes();
        StringBuffer attributesXml = new StringBuffer();

        for (String attributeName : attributes.keySet()) {
            attributesXml.append(constructSamlAttribute(attributeName.toLowerCase(), attributes.get(attributeName).toString()));
        }

        samlResponse = samlResponse.replace("<ATTRIBUTES>", attributesXml.toString());

//        log.debug("about to sign SAMLv2 response: " + samlResponse);
//        samlResponse = SamlHelper.signAssertion(samlResponse, publicKey, privateKey);
//        log.debug("returning signed SAMLv2 response: " + samlResponse);

        return samlResponse;
    }

    private String constructSamlAttribute(String name, String value) {
        try {
            StringBuffer saml = new StringBuffer();

            saml.append("<Attribute NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:basic\" Name=\"" + StringEscapeUtils.escapeXml(name) + "\">");
            saml.append("  <AttributeValue xsi:type=\"xs:string\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
            saml.append(StringEscapeUtils.escapeXml(value));
            saml.append("  </AttributeValue>");
            saml.append("</Attribute>");

            return saml.toString();
        } catch (Exception e) {
            log.error("failed to construct SAML attribute for name=" + name + ", value=" + value, e);

            return "";
        }
    }

    private static String createID() {
        final byte[] bytes = new byte[20]; // 160 bits
        random.nextBytes(bytes);

        final char[] chars = new char[40];

        for (int i = 0; i < bytes.length; i++) {
            int left = (bytes[i] >> 4) & 0x0f;
            int right = bytes[i] & 0x0f;
            chars[i * 2] = charMapping[left];
            chars[i * 2 + 1] = charMapping[right];
        }

        return String.valueOf(chars);
    }

    /**
     * Attempts to decode the Base64-encoded, possibly compressed, XML authentication request.
     */
    private static String decodeAuthnRequestXML(final String encodedRequestXmlString) {
        log.debug("attempting to decode authentication request from encoded XML: " + encodedRequestXmlString);

        if (encodedRequestXmlString == null) {
            return null;
        }

        final byte[] decodedBytes = base64Decode(encodedRequestXmlString);

        if (decodedBytes == null) {
            return null;
        }

        final String inflated = inflate(decodedBytes);

        if (inflated != null) {
            log.debug("decompressed XML: " + inflated);

            return inflated;
        }

        log.debug("attempting to zlibDeflate");

        final String deflated = zlibDeflate(decodedBytes);

        if (deflated != null) {
            log.debug("deflated XML: " + deflated);

            return deflated;
        }

        try {
            return new String(decodedBytes, "UTF-8");
        } catch (Exception e) {
            log.debug("unable to make any sense out of the authentication request", e);

            return null;
        }
    }

    private static String zlibDeflate(final byte[] bytes) {
        final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final InflaterInputStream iis = new InflaterInputStream(bais);
        final byte[] buf = new byte[1024];

        try {
            int count = iis.read(buf);
            while (count != -1) {
                baos.write(buf, 0, count);
                count = iis.read(buf);
            }
            return new String(baos.toByteArray());
        } catch (final Exception e) {
            log.debug("unable to deflate bytes; this probably isn't zipped content");

            return null;
        } finally {
            try {
                iis.close();
            } catch (final Exception e) {
                // nothing to do
            }
        }
    }

    private static byte[] base64Decode(final String xml) {
        try {
            final byte[] xmlBytes = xml.getBytes("UTF-8");
            return Base64.decodeBase64(xmlBytes);
        } catch (final Exception e) {
            log.debug("Base64 decoding failed", e);

            return null;
        }
    }

    private static String inflate(final byte[] bytes) {
        final Inflater inflater = new Inflater(true);
        final byte[] xmlMessageBytes = new byte[10000];

        final byte[] extendedBytes = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, extendedBytes, 0, bytes.length);
        extendedBytes[bytes.length] = 0;

        inflater.setInput(extendedBytes);

        try {
            final int resultLength = inflater.inflate(xmlMessageBytes);
            inflater.end();

            if (!inflater.finished()) {
                throw new RuntimeException("buffer not large enough.");
            }

            inflater.end();
            return new String(xmlMessageBytes, 0, resultLength, "UTF-8");
        } catch (final DataFormatException e) {
            return null;
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Cannot find encoding: UTF-8", e);
        }
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
