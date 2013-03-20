package com.infusionsoft.cas.support;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

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
import java.util.Collections;

/**
 * Helper class for SAMLv2.
 */
public class SamlHelper {
    private static final Logger log = Logger.getLogger(SamlHelper.class);

    /**
     * Props to the creators of Java for creating such an easy and straightforward way to sign an XML document.
     */
    public static String signAssertion(String samlResponse, PublicKey publicKey, PrivateKey privateKey) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setNamespaceAware(true);

            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(samlResponse)));
            Node assertion = document.getElementsByTagName("Assertion").item(0);
            DOMSignContext dsc = new DOMSignContext(privateKey, assertion);
            XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
            Reference ref = signatureFactory.newReference("", signatureFactory.newDigestMethod(DigestMethod.SHA1, null), Collections.singletonList(signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, null);
            SignedInfo si = signatureFactory.newSignedInfo(signatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null), signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(ref));
            KeyInfoFactory kif = signatureFactory.getKeyInfoFactory();
            KeyValue kv = kif.newKeyValue(publicKey);
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));
            XMLSignature signature = signatureFactory.newXMLSignature(si, ki);

            signature.sign(dsc);

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
