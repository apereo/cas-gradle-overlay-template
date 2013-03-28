package com.infusionsoft.cas.support;

import org.apache.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.bind.Element;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class for SAMLv2.
 */
public class SamlHelper {
    private static final Logger log = Logger.getLogger(SamlHelper.class);

    private static void setPrefixOnElements(Document document, String tagName, String prefix) {
        NodeList list =  document.getElementsByTagName(tagName);

        for (int i = 0; i < list.getLength(); i++) {
            Node element = list.item(i);

            element.setPrefix(prefix);
        }
    }

    /**
     * Props to the creators of Java for creating such an easy and straightforward way to sign an XML document.
     */
    public static String signAssertion(String samlResponse, PrivateKey privateKey, PublicKey publicKey) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setNamespaceAware(true);

            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(samlResponse)));
            Node assertion = document.getElementsByTagName("Assertion").item(0);
            String id = assertion.getAttributes().getNamedItem("ID").getNodeValue();

            log.debug("signing assertion with URI " + id);

            setPrefixOnElements(document, "Issuer", "saml");
            setPrefixOnElements(document, "Assertion", "saml");
            setPrefixOnElements(document, "Subject", "saml");
            setPrefixOnElements(document, "SubjectConfirmation", "saml");
            setPrefixOnElements(document, "SubjectConfirmationData", "saml");
            setPrefixOnElements(document, "Conditions", "saml");
            setPrefixOnElements(document, "NameID", "saml");
            setPrefixOnElements(document, "Conditions", "saml");
            setPrefixOnElements(document, "AudienceRestriction", "saml");
            setPrefixOnElements(document, "Audience", "saml");
            setPrefixOnElements(document, "AuthnStatement", "saml");
            setPrefixOnElements(document, "AuthnContext", "saml");
            setPrefixOnElements(document, "AuthnContextClassRef", "saml");
            setPrefixOnElements(document, "AttributeStatement", "saml");
            setPrefixOnElements(document, "Attribute", "saml");
            setPrefixOnElements(document, "AttributeValue", "saml");
            setPrefixOnElements(document, "AttributeStatement", "saml");

            DOMSignContext dsc = new DOMSignContext(privateKey, assertion);
            XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
            List transforms = new ArrayList();

            transforms.add(signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
            transforms.add(signatureFactory.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#",(TransformParameterSpec) null));

            Reference ref = signatureFactory.newReference("#" + id, signatureFactory.newDigestMethod(DigestMethod.SHA1, null), transforms, null, null);
            SignedInfo si = signatureFactory.newSignedInfo(signatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null), signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));
            KeyInfoFactory kif = signatureFactory.getKeyInfoFactory();
            KeyValue kv = kif.newKeyValue(publicKey);
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

            //XMLSignature signature = signatureFactory.newXMLSignature(si, ki);
            XMLSignature signature = signatureFactory.newXMLSignature(si, null);

            signature.sign(dsc);

            Node signatureNode = document.getElementsByTagNameNS("*", "Signature").item(0);
            Node subjectNode = document.getElementsByTagNameNS("*", "Subject").item(0);

            assertion.removeChild(signatureNode);
            assertion.insertBefore(signatureNode, subjectNode);

            // Look at all the silly nonsense we have to do for Mashery
            setPrefixOnElements(document, "Signature", "ds");
            setPrefixOnElements(document, "SignedInfo", "ds");
            setPrefixOnElements(document, "CanonicalizationMethod", "ds");
            setPrefixOnElements(document, "SignatureMethod", "ds");
            setPrefixOnElements(document, "Reference", "ds");
            setPrefixOnElements(document, "Transforms", "ds");
            setPrefixOnElements(document, "Transform", "ds");
            setPrefixOnElements(document, "DigestMethod", "ds");
            setPrefixOnElements(document, "DigestValue", "ds");
            setPrefixOnElements(document, "SignatureValue", "ds");

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();

            trans.transform(new DOMSource(document), new StreamResult(output));

            return new String(output.toByteArray(), "UTF-8");
        } catch (Exception e) {
            log.error("failed to sign SAMLv2 assertion!", e);

            return samlResponse;
        }
    }
}
