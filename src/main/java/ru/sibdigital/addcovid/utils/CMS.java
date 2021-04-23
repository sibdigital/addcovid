/**
 * $RCSfile$
 * version $Revision$
 * created 15.08.2007 11:42:12 by kunina
 * last modified $Date$ by $Author$
 * (C) ООО Крипто-Про 2004-2007.
 */
package ru.sibdigital.addcovid.utils;

import com.objsys.asn1j.runtime.*;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.*;
import ru.CryptoPro.JCP.ASN.PKIX1Explicit88.CertificateSerialNumber;
import ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Name;
import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.Util.JCPInit;
import ru.CryptoPro.JCP.params.OID;
import ru.CryptoPro.JCP.tools.AlgorithmUtility;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Base64;


/**
 * CMS sign and verify
 * <br>
 * csptest -sfsign -in data.txt -my key -sign -out data.sgn
 * <br>
 * csptest -sfsign -my key -verify -in data.sgn
 * <br>
 * csptest -lowsign -in data.txt -my key -sign -out data_low.sgn
 * <br>
 * csptest -lowsign -in data.sgn -verify ???
 *
 * @author Copyright 2004-2009 Crypto-Pro. All rights reserved.
 * @version 2.5
 */
public class CMS {

    public static final String STR_CMS_OID_DATA = "1.2.840.113549.1.7.1";
    public static final String STR_CMS_OID_SIGNED = "1.2.840.113549.1.7.2";

    /**
     * Конструктор.
     *
     */
    private CMS() {
        ;
    }

    /**
     * main Sign / Verify
     *
     * @param args //
     * @throws Exception e
     */
    public static void main(String[] args) throws Exception {

        JCPInit.initProviders(false);

        final byte[] data = "this is test message".getBytes(StandardCharsets.UTF_8);
        final PrivateKey key = loadKey("ХамархановаИК 2021-02-05 10-31-59", "123456".toCharArray());
        final Certificate cert = loadCertificate("ХамархановаИК 2021-02-05 10-31-59");

        //Sign
        byte[] sign = CMSSignEx(data, key, cert, true, JCP.PROVIDER_NAME);
        System.out.println("Подпись (в формате base64): " + Base64.getEncoder().encodeToString(sign));

        //Verify
        boolean checkResult = CMSVerifyEx(sign, cert, data, JCP.PROVIDER_NAME);
        System.out.println(checkResult);
    }

    /**
     * sign CMS
     *
     * @param data data
     * @param key key
     * @param cert cert
     * @throws Exception e
     */
    public static byte[] CMSSign(byte[] data, PrivateKey key,
                                 Certificate cert, boolean detached) throws Exception {
        return CMSSignEx(data, key, cert, detached, JCP.PROVIDER_NAME);
    }

    /**
     * sign CMS
     *
     * @param data data
     * @param key key
     * @param cert cert
     * @param detached detached signature
     * @param providerName provider name
     * @throws Exception e
     * @since 2.0
     */
    public static byte[] CMSSignEx(byte[] data, PrivateKey key,
                                   Certificate cert, boolean detached, String providerName)
            throws Exception {

        String keyAlg = key.getAlgorithm();
        String signOid = AlgorithmUtility.keyAlgToSignatureOid(keyAlg);

        // sign
        final Signature signature = Signature.getInstance(signOid, providerName);
        signature.initSign(key);
        signature.update(data);

        final byte[] sign = signature.sign();

        // create cms format
        return createCMSEx(data, sign, cert, detached);
    }

    /**
     * createCMS
     *
     * @param buffer buffer
     * @param sign sign
     * @param cert cert
     * @param detached detached signature
     * @return byte[]
     * @throws Exception e
     * @since 2.0
     */
    public static byte[] createCMSEx(byte[] buffer, byte[] sign,
                                     Certificate cert, boolean detached) throws Exception {

        String pubKeyAlg = cert.getPublicKey().getAlgorithm();
        String digestOid = AlgorithmUtility.keyAlgToDigestOid(pubKeyAlg);
        String keyOid = AlgorithmUtility.keyAlgToKeyAlgorithmOid(pubKeyAlg); // алгоритм ключа подписи

        final ContentInfo all = new ContentInfo();
        all.contentType = new Asn1ObjectIdentifier(
                new OID(STR_CMS_OID_SIGNED).value);

        final SignedData cms = new SignedData();
        all.content = cms;
        cms.version = new CMSVersion(1);

        // digest
        cms.digestAlgorithms = new DigestAlgorithmIdentifiers(1);
        final DigestAlgorithmIdentifier a = new DigestAlgorithmIdentifier(
                new OID(digestOid).value);

        a.parameters = new Asn1Null();
        cms.digestAlgorithms.elements[0] = a;

        if (detached) {
            cms.encapContentInfo = new EncapsulatedContentInfo(
                    new Asn1ObjectIdentifier(
                            new OID(STR_CMS_OID_DATA).value), null);
        } // if
        else {
            cms.encapContentInfo =
                    new EncapsulatedContentInfo(new Asn1ObjectIdentifier(
                            new OID(STR_CMS_OID_DATA).value),
                            new Asn1OctetString(buffer));
        } // else

        // certificate
        cms.certificates = new CertificateSet(1);
        final ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Certificate certificate =
                new ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Certificate();
        final Asn1BerDecodeBuffer decodeBuffer =
                new Asn1BerDecodeBuffer(cert.getEncoded());
        certificate.decode(decodeBuffer);

        cms.certificates.elements = new CertificateChoices[1];
        cms.certificates.elements[0] = new CertificateChoices();
        cms.certificates.elements[0].set_certificate(certificate);

        // signer info
        cms.signerInfos = new SignerInfos(1);
        cms.signerInfos.elements[0] = new SignerInfo();
        cms.signerInfos.elements[0].version = new CMSVersion(1);
        cms.signerInfos.elements[0].sid = new SignerIdentifier();

        final byte[] encodedName = ((X509Certificate) cert)
                .getIssuerX500Principal().getEncoded();
        final Asn1BerDecodeBuffer nameBuf = new Asn1BerDecodeBuffer(encodedName);
        final Name name = new Name();
        name.decode(nameBuf);

        final CertificateSerialNumber num = new CertificateSerialNumber(
                ((X509Certificate) cert).getSerialNumber());
        cms.signerInfos.elements[0].sid.set_issuerAndSerialNumber(
                new IssuerAndSerialNumber(name, num));
        cms.signerInfos.elements[0].digestAlgorithm =
                new DigestAlgorithmIdentifier(new OID(digestOid).value);
        cms.signerInfos.elements[0].digestAlgorithm.parameters = new Asn1Null();
        cms.signerInfos.elements[0].signatureAlgorithm =
                new SignatureAlgorithmIdentifier(new OID(keyOid).value);
        cms.signerInfos.elements[0].signatureAlgorithm.parameters = new Asn1Null();
        cms.signerInfos.elements[0].signature = new SignatureValue(sign);

        // encode
        final Asn1BerEncodeBuffer asnBuf = new Asn1BerEncodeBuffer();
        all.encode(asnBuf, true);
        return asnBuf.getMsgCopy();
    }

    /**
     * verify CMS
     *
     * @param buffer buffer
     * @param cert cert
     * @param data data
     * @return boolean
     * @throws Exception e
     * @deprecated начиная с версии 1.0.54, следует использовать
     * функцонал CAdES API (CAdES.jar)
     */
    public static boolean CMSVerify(byte[] buffer, Certificate cert,
                                    byte[] data) throws Exception {
        return CMSVerifyEx(buffer, cert, data, JCP.PROVIDER_NAME);
    }

    /**
     * verify CMS
     *
     * @param buffer buffer
     * @param cert cert
     * @param data data
     * @param providerName provider name
     * @return boolean
     * @throws Exception e
     * @deprecated начиная с версии 1.0.54, следует использовать
     * функцонал CAdES API (CAdES.jar)
     * @since 2.0
     */
    public static boolean CMSVerifyEx(byte[] buffer, Certificate cert,
                                      byte[] data, String providerName) throws Exception {

        String certPubKeyAlg = cert.getPublicKey().getAlgorithm();
        String certDigestOid = AlgorithmUtility.keyAlgToDigestOid(certPubKeyAlg);
        String signOid = AlgorithmUtility.keyAlgToSignatureOid(certPubKeyAlg);

        int i;
        final Asn1BerDecodeBuffer asnBuf = new Asn1BerDecodeBuffer(buffer);
        final ContentInfo all = new ContentInfo();
        all.decode(asnBuf);

        if (!new OID(STR_CMS_OID_SIGNED).eq(all.contentType.value)) {
            throw new Exception("Not supported");
        } // if

        final SignedData cms = (SignedData) all.content;
        if (cms.version.value != 1) {
            throw new Exception("Incorrect version");
        } // if

        if (!new OID(STR_CMS_OID_DATA).eq(
                cms.encapContentInfo.eContentType.value)) {
            throw new Exception("Nested not supported");
        } // if

        byte[] text = null;
        if (data != null) {
            text = data;
        } // if
        else if (cms.encapContentInfo.eContent != null) {
            text = cms.encapContentInfo.eContent.value;
        } // else

        if (text == null) {
            throw new Exception("No content");
        } // if

        OID digestOid = null;
        DigestAlgorithmIdentifier a = new DigestAlgorithmIdentifier(
                new OID(certDigestOid).value);

        for (i = 0; i < cms.digestAlgorithms.elements.length; i++) {
            if (cms.digestAlgorithms.elements[i].algorithm.equals(a.algorithm)) {
                digestOid = new OID(cms.digestAlgorithms.elements[i].algorithm.value);
                break;
            } // if
        } // for

        if (digestOid == null) {
            throw new Exception("Unknown digest");
        } // if

        int pos = -1;

        if (cms.certificates != null) {

            for (i = 0; i < cms.certificates.elements.length; i++) {

                final Asn1BerEncodeBuffer encBuf = new Asn1BerEncodeBuffer();
                cms.certificates.elements[i].encode(encBuf);
                final byte[] in = encBuf.getMsgCopy();

                if (Arrays.equals(in, cert.getEncoded())) {
                    System.out.println("Certificate: " + ((X509Certificate) cert).getSubjectDN());
                    pos = i;
                    break;
                } // if

            } // for

            if (pos == -1) {
                throw new Exception("Not signed on certificate.");
            } // if

        } else if (cert == null) {
            throw new Exception("No certificate found.");
        } // else
        else {
            // Если задан {@link #cert}, то пробуем проверить
            // первую же подпись на нем.
            pos = 0;
        } // else

        final SignerInfo info = cms.signerInfos.elements[pos];
        if (info.version.value != 1) {
            throw new Exception("Incorrect version");
        } // if

        if (!digestOid.equals(new OID(info.digestAlgorithm.algorithm.value))) {
            throw new Exception("Not signed on certificate.");
        } // if

        final byte[] sign = info.signature.value;

        // check
        final Signature signature = Signature.getInstance(signOid, providerName);
        signature.initVerify(cert);
        signature.update(text);

        final boolean checkResult = signature.verify(sign);

        return checkResult;
    }

    /**
     * Получение PrivateKey из store.
     *
     * @param name     alias ключа
     * @param password пароль на ключ
     * @return PrivateKey
     * @throws Exception in key read
     */
    public static PrivateKey loadKey(String name, char[] password) throws Exception {
        final KeyStore hdImageStore = KeyStore.getInstance(
                JCP.HD_STORE_NAME, JCP.PROVIDER_NAME);
        hdImageStore.load(null, null);
        return (PrivateKey) hdImageStore.getKey(name, password);
    }

    /**
     * Получение certificate из store.
     *
     * @param name alias сертификата.
     * @return Certificate
     * @throws Exception in cert read
     */
    public static Certificate loadCertificate(String name) throws Exception {
        final KeyStore hdImageStore = KeyStore.getInstance(
                JCP.HD_STORE_NAME, JCP.PROVIDER_NAME);
        hdImageStore.load(null, null);
        return hdImageStore.getCertificate(name);
    }
}