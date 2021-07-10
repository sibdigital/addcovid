/**
 * $RCSfile$
 * version $Revision$
 * created 15.08.2007 11:42:12 by kunina
 * last modified $Date$ by $Author$
 * (C) ООО Крипто-Про 2004-2007.
 */
package ru.sibdigital.addcovid.utils;

import com.objsys.asn1j.runtime.*;
import ru.CryptoPro.JCP.ASN.CertificateExtensions.GeneralName;
import ru.CryptoPro.JCP.ASN.CertificateExtensions.GeneralNames;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.*;
import ru.CryptoPro.JCP.ASN.PKIX1Explicit88.*;
import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.Util.JCPInit;
import ru.CryptoPro.JCP.params.OID;
import ru.CryptoPro.JCP.tools.AlgorithmUtility;
import ru.CryptoPro.JCP.tools.Array;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


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

    public static final String STR_CMS_OID_CONT_TYP_ATTR = "1.2.840.113549.1.9.3";
    public static final String STR_CMS_OID_DIGEST_ATTR = "1.2.840.113549.1.9.4";
    public static final String STR_CMS_OID_SIGN_TYM_ATTR = "1.2.840.113549.1.9.5";

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

//        final byte[] data = "this is test message".getBytes(StandardCharsets.UTF_8);
//        final PrivateKey key = loadKey("ХамархановаИК 2021-02-05 10-31-59", "123456".toCharArray());
//        final Certificate cert = loadCertificate("ХамархановаИК 2021-02-05 10-31-59");
//
//        //Sign
//        byte[] sign = CMSSignEx(data, key, cert, true, JCP.PROVIDER_NAME);
//        System.out.println("Подпись (в формате base64): " + Base64.getEncoder().encodeToString(sign));
//
//        //Verify
//        boolean checkResult = CMSVerifyEx(sign, cert, data, JCP.PROVIDER_NAME);
//        System.out.println(checkResult);

        byte[] sign = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("/home/sergey/pkcs7/Заявление_№351573-Заявление о включении сведений в единый реестр.pdf.p7s");
            sign = fis.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        byte[] data = null;
        fis = null;
        try {
            fis = new FileInputStream("/home/sergey/pkcs7/Заявление_№351573-Заявление о включении сведений в единый реестр.pdf");
            data = fis.readAllBytes();
        } catch (Exception e) {
            //
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        Certificate[] certs = null;

        boolean checkResult = CMSVerify(sign, certs, data);
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

    /**
     * проверка CMS
     *
     * @param buffer буфер
     * @param certs сертификаты
     * @param data данные
     * @throws Exception e
     *
     * @deprecated начиная с версии 1.0.54, следует использовать функцонал CAdES API (CAdES.jar)
     */
    public static boolean CMSVerify(byte[] buffer, Certificate[] certs,
                                 byte[] data) throws Exception {
        return CMSVerifyEx(buffer, certs, data, JCP.PROVIDER_NAME);
    }

    /**
     * проверка CMS
     *
     * @param buffer буфер
     * @param certs сертификаты
     * @param data данные
     * @param providerName имя провайдера
     * @throws Exception e
     *
     * @deprecated начиная с версии 1.0.54, следует использовать функцонал CAdES API (CAdES.jar)
     * @since 2.0
     */
    public static boolean CMSVerifyEx(byte[] buffer, Certificate[] certs,
                                   byte[] data, String providerName) throws Exception {

        String certDigestOid = null;
        String signAlg       = null;

        // clear buffers fo logs
        StringBuffer out = new StringBuffer("");
        StringBuffer out1 = new StringBuffer("");

        final Asn1BerDecodeBuffer asnBuf = new Asn1BerDecodeBuffer(buffer);
        final ContentInfo all = new ContentInfo();
        all.decode(asnBuf);

        if (!new OID(STR_CMS_OID_SIGNED).eq(all.contentType.value)) {
            throw new Exception("Not supported");
        } // if

        final SignedData cms = (SignedData) all.content;
        final byte[] text;

        if (cms.encapContentInfo.eContent != null) {
            text = cms.encapContentInfo.eContent.value;
        } // if
        else if (data != null) {
            text = data;
        } // else
        else {
            throw new Exception("No content for verify");
        } // else

//        if(CMStools.logger != null) {
//            CMStools.logger.info("Source data: " + new String(text));
//        }

        // Список oid алгоритмов хеширования, если подписантов несколько
        final List<OID> digestOidList = new LinkedList<OID>(); // соответствует списку подписантов

        OID digestOid = null;
        DigestAlgorithmIdentifier digestAlgorithmIdentifier = null;

        if (certDigestOid != null) {

            digestAlgorithmIdentifier = new DigestAlgorithmIdentifier(
                    new OID(certDigestOid).value);

        } // if

        for (int i = 0; i < cms.digestAlgorithms.elements.length; i++) {

            OID currentOid = new OID(cms.digestAlgorithms.elements[i].algorithm.value);

            if (digestAlgorithmIdentifier != null &&
                    cms.digestAlgorithms.elements[i].algorithm
                            .equals(digestAlgorithmIdentifier.algorithm)) {

                digestOid = currentOid;
                break; // сразу нашли

            } // if
            else {
                digestOidList.add(currentOid);
            } // else

        } // for

        if (digestOid == null && digestOidList.isEmpty()) {
            throw new Exception("Unknown digest");
        } // if

        int validsign = 0;
        final OID eContTypeOID = new OID(cms.encapContentInfo.eContentType.value);
        if (cms.certificates != null) {

            // Проверка на вложенных сертификатах
//            if (CMStools.logger != null) {
//                CMStools.logger.info("Validation on certificates founded in CMS.");
//            }

            for (int i = 0; i < cms.certificates.elements.length; i++) {

                final Asn1BerEncodeBuffer encBuf = new Asn1BerEncodeBuffer();
                cms.certificates.elements[i].encode(encBuf);

                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                final X509Certificate cert = (X509Certificate) cf
                        .generateCertificate(encBuf.getInputStream());

                for (int j = 0; j < cms.signerInfos.elements.length; j++) {

                    final SignerInfo info = cms.signerInfos.elements[j];
                    final OID currentDigestOid = new OID(info.digestAlgorithm.algorithm.value);
                    OID expectedDigestOid = null;

                    if (digestOid != null) {
                        expectedDigestOid = digestOid;
                    } // if
                    else {
                        for (int n = 0; n < digestOidList.size(); n++) {
                            if (digestOidList.get(n).equals(currentDigestOid)) {
                                expectedDigestOid = currentDigestOid;
                                break;
                            } // if
                        }

                    } // else

                    if (expectedDigestOid == null) {
                        throw new Exception("Not signed on certificate.");
                    } // if

                    SignatureAlgorithmIdentifier expectedSignId = info.signatureAlgorithm;
                    String expectedSignAlg;

                    if (signAlg == null) {

                        OID signAlgOid  = new OID(expectedSignId.algorithm.value);
                        expectedSignAlg = signAlgOid.toString();

                    } // if
                    else {
                        expectedSignAlg = signAlg;
                    } // else

                    expectedSignAlg = validateSignatureAlgorithm(expectedSignAlg);

                    final boolean checkResult = verifyOnCert(cert,
                            cms.signerInfos.elements[j], text, eContTypeOID,
                            true, currentDigestOid, expectedSignAlg, providerName);

//                    writeLog(checkResult, j, i, cert);

                    if (checkResult) {
                        validsign++;
                    } // if

                } // for

            } // for

        } // if
        else if (certs != null) {

            // Проверка на указанных сертификатах
//            if (CMStools.logger != null) {
//                CMStools.logger.info("Certificates for validation not found in CMS.\n" +
//                        "Try verify on specified certificates...");
//            }

            for (int i = 0; i < certs.length; i++) {

                final X509Certificate cert = (X509Certificate) certs[i];
                for (int j = 0; j < cms.signerInfos.elements.length; j++) {

                    final SignerInfo info = cms.signerInfos.elements[j];
                    final OID currentDigestOid = new OID(info.digestAlgorithm.algorithm.value);
                    OID expectedDigestOid = null;

                    if (digestOid != null) {
                        expectedDigestOid = digestOid;
                    } // if
                    else {
                        for (int n = 0; n < digestOidList.size(); n++) {
                            if (digestOidList.get(n).equals(currentDigestOid)) {
                                expectedDigestOid = currentDigestOid;
                                break;
                            } // if
                        }
                    } // else

                    if (expectedDigestOid == null) {
                        throw new Exception("Not signed on certificate.");
                    } // if

                    SignatureAlgorithmIdentifier expectedSignId = info.signatureAlgorithm;
                    String expectedSignAlg;

                    if (signAlg == null) {

                        OID signAlgOid  = new OID(expectedSignId.algorithm.value);
                        expectedSignAlg = signAlgOid.toString();

                    } // if
                    else {
                        expectedSignAlg = signAlg;
                    } // else

                    expectedSignAlg = validateSignatureAlgorithm(expectedSignAlg);

                    final boolean checkResult = verifyOnCert(cert,
                            cms.signerInfos.elements[j], text, eContTypeOID,
                            true, currentDigestOid, expectedSignAlg, providerName);

//                    writeLog(checkResult, j, i, cert);

                    if (checkResult) {
                        validsign++;
                    } // if

                } // for

            } // for

        } // else
        else {
//            if (CMStools.logger != null) {
//                CMStools.logger.warning("Certificates for validation not found");
//            }
        } // else

        if (validsign == 0) {
            throw new Exception("Signatures are invalid: " + out1);
        } // if

        if (cms.signerInfos.elements.length > validsign) {
            throw new Exception("Some signatures are invalid: " + out + out1);
        } // if
//        else {
//            if (CMStools.logger != null) {
//                CMStools.logger.info("All signatures are valid: " + out);
//            }
//        } // else

        return true;
    }


    /**
     * Определение алгоритма подписи.
     *
     * @param signAlg Алгоритм подписи.
     * @return исправленный алгоритм подписи.
     */
    private static String validateSignatureAlgorithm(String signAlg) {

        // В signAlg может быть передан как алгоритм подписи...

        if (signAlg.equalsIgnoreCase(JCP.GOST_EL_SIGN_NAME)
                || signAlg.equalsIgnoreCase(JCP.GOST_DHEL_SIGN_NAME)
                || signAlg.equalsIgnoreCase(JCP.GOST_SIGN_2012_256_NAME)
                || signAlg.equalsIgnoreCase(JCP.GOST_SIGN_DH_2012_256_NAME)
                || signAlg.equalsIgnoreCase(JCP.GOST_SIGN_2012_512_NAME)
                || signAlg.equalsIgnoreCase(JCP.GOST_SIGN_DH_2012_512_NAME)) {
            return signAlg;
        } // if

        //...так и oid алгоритма подписи...

        if (signAlg.equalsIgnoreCase(JCP.GOST_EL_SIGN_OID)
                || signAlg.equalsIgnoreCase(JCP.GOST_SIGN_2012_256_OID)
                || signAlg.equalsIgnoreCase(JCP.GOST_SIGN_2012_512_OID)) {
            return signAlg;
        } // if

        //...или oid ключа, по которому надо определить
        // алгоритм подписи.

        if (signAlg.equalsIgnoreCase(JCP.GOST_EL_KEY_OID)
                || signAlg.equalsIgnoreCase(JCP.GOST_EL_DH_OID)
                || signAlg.equalsIgnoreCase("1.2.643.2.2.20")) {
            return JCP.GOST_EL_SIGN_NAME;
        } // if

        if (signAlg.equalsIgnoreCase(JCP.GOST_PARAMS_SIG_2012_256_KEY_OID)
                || signAlg.equalsIgnoreCase(JCP.GOST_PARAMS_EXC_2012_256_KEY_OID)) {
            return JCP.GOST_SIGN_2012_256_NAME;
        } // if

        if (signAlg.equalsIgnoreCase(JCP.GOST_PARAMS_SIG_2012_512_KEY_OID)
                || signAlg.equalsIgnoreCase(JCP.GOST_PARAMS_EXC_2012_512_KEY_OID)) {
            return JCP.GOST_SIGN_2012_512_NAME;
        } // if

        return signAlg;

    }

    /**
     * Попытка проверки подписи на указанном сертификате.
     * Проверка может быть выполнена как по отсортированным
     * подписанным аттрибутам, так и по несортированным.
     *
     * @param cert сертификат для проверки
     * @param text текст для проверки
     * @param info подпись
     * @param eContentTypeOID тип содержимого
     * @param needSortSignedAttributes True, если необходимо проверить
     * подпись по отсортированным подписанным аттрибутам. По умолчанию
     * подписанные аттрибуты сортируются перед кодированием.
     * @param digestAlgOid Алгоритм хеширования.
     * @param signAlgOid Алгоритм подписи.
     * @param providerName Имя провайдера.
     * @return верна ли подпись
     * @throws Exception ошибки
     */
    private static boolean verifyOnCert(X509Certificate cert, SignerInfo info,
                                        byte[] text, OID eContentTypeOID, boolean needSortSignedAttributes,
                                        OID digestAlgOid, String signAlgOid, String providerName)
            throws Exception {

        // подпись
        final byte[] sign = info.signature.value;

        // данные для проверки подписи
        final byte[] data;

        if (info.signedAttrs == null) {
            // аттрибуты подписи не присутствуют
            // данные для проверки подписи
            data = text;
        } // if
        else {

            // присутствуют аттрибуты подписи (SignedAttr)
            final Attribute[] signAttrElem = info.signedAttrs.elements;

            // проверка аттрибута signing-certificateV2
            final Asn1ObjectIdentifier signingCertificateV2Oid = new Asn1ObjectIdentifier(
                    (new OID(ALL_PKIX1Explicit88Values.id_aa_signingCertificateV2)).value);
            Attribute signingCertificateV2Attr = null;

            for (int r = 0; r < signAttrElem.length; r++) {
                final Asn1ObjectIdentifier oid = signAttrElem[r].type;
                if (oid.equals(signingCertificateV2Oid)) {
                    signingCertificateV2Attr = signAttrElem[r];
                } // if
            } // for

            if (signingCertificateV2Attr != null) {

                SigningCertificateV2 signingCertificateV2 = (SigningCertificateV2)
                        signingCertificateV2Attr.values.elements[0];
                _SeqOfESSCertIDv2 essCertIDv2s = signingCertificateV2.certs;

                for (int s = 0; s < essCertIDv2s.elements.length; s++) {

                    ESSCertIDv2 essCertIDv2 = essCertIDv2s.elements[s];

                    CertHash expectedCertHash = essCertIDv2.certHash;
                    AlgorithmIdentifier expectedHashAlgorithm = essCertIDv2.hashAlgorithm;

                    IssuerSerial expectedIssuerSerial = essCertIDv2.issuerSerial;
                    Asn1BerEncodeBuffer encodedExpectedIssuerSerial = new Asn1BerEncodeBuffer();
                    expectedIssuerSerial.encode(encodedExpectedIssuerSerial);

                    OID expectedHashAlgorithmOid = new OID(expectedHashAlgorithm.algorithm.value);
                    CertHash actualCertHash = new CertHash(digestm(cert.getEncoded(),
                            expectedHashAlgorithmOid.toString(), providerName));

                    ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Certificate certificate =
                            new ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Certificate();
                    Asn1BerDecodeBuffer decodeBuffer =
                            new Asn1BerDecodeBuffer(cert.getEncoded());
                    certificate.decode(decodeBuffer);

                    GeneralName[] issuerName = new GeneralName[1];
                    issuerName[0] = new GeneralName(GeneralName._DIRECTORYNAME,
                            certificate.tbsCertificate.issuer);
                    GeneralNames issuerNames = new GeneralNames(issuerName);

                    IssuerSerial actualIssuerSerial = new IssuerSerial(issuerNames,
                            certificate.tbsCertificate.serialNumber);
                    Asn1BerEncodeBuffer encodedActualIssuerSerial = new Asn1BerEncodeBuffer();
                    actualIssuerSerial.encode(encodedActualIssuerSerial);

                    if ( !(Arrays.equals(actualCertHash.value, expectedCertHash.value) &&
                            Arrays.equals(encodedActualIssuerSerial.getMsgCopy(),
                                    encodedActualIssuerSerial.getMsgCopy())) ) {

                        System.out.println("Certificate stored in signing-certificateV2 " +
                                "is not equal to " + cert.getSubjectDN());
                        return false;
                    } // if

                } // for

            } // if

            // проверка аттрибута content-type
            final Asn1ObjectIdentifier contentTypeOid = new Asn1ObjectIdentifier(
                    (new OID(STR_CMS_OID_CONT_TYP_ATTR)).value);
            Attribute contentTypeAttr = null;

            for (int r = 0; r < signAttrElem.length; r++) {
                final Asn1ObjectIdentifier oid = signAttrElem[r].type;
                if (oid.equals(contentTypeOid)) {
                    contentTypeAttr = signAttrElem[r];
                } // if
            } // for

            if (contentTypeAttr == null) {
                throw new Exception("content-type attribute not present");
            } // if

            if (!contentTypeAttr.values.elements[0]
                    .equals(new Asn1ObjectIdentifier(eContentTypeOID.value))) {
                throw new Exception("content-type attribute OID not equal eContentType OID");
            } // if

            // проверка аттрибута message-digest
            final Asn1ObjectIdentifier messageDigestOid = new Asn1ObjectIdentifier(
                    (new OID(STR_CMS_OID_DIGEST_ATTR)).value);

            Attribute messageDigestAttr = null;

            for (int r = 0; r < signAttrElem.length; r++) {
                final Asn1ObjectIdentifier oid = signAttrElem[r].type;
                if (oid.equals(messageDigestOid)) {
                    messageDigestAttr = signAttrElem[r];
                } // if
            } // for

            if (messageDigestAttr == null) {
                throw new Exception("message-digest attribute not present");
            } // if

            final Asn1Type open = messageDigestAttr.values.elements[0];
            final Asn1OctetString hash = (Asn1OctetString) open;
            final byte[] md = hash.value;

            // вычисление messageDigest
            final byte[] dm = digestm(text, digestAlgOid.toString(), providerName);

            if (!Array.toHexString(dm).equals(Array.toHexString(md))) {
                throw new Exception("message-digest attribute verify failed");
            } // if

            // проверка аттрибута signing-time
            final Asn1ObjectIdentifier signTimeOid = new Asn1ObjectIdentifier(
                    (new OID(STR_CMS_OID_SIGN_TYM_ATTR)).value);

            Attribute signTimeAttr = null;

            for (int r = 0; r < signAttrElem.length; r++) {
                final Asn1ObjectIdentifier oid = signAttrElem[r].type;
                if (oid.equals(signTimeOid)) {
                    signTimeAttr = signAttrElem[r];
                } // if
            } // for

            if (signTimeAttr != null) {
                // проверка (необязательно)
                Time sigTime = (Time)signTimeAttr.values.elements[0];
                Asn1UTCTime time = (Asn1UTCTime) sigTime.getElement();
                System.out.println("Signing Time: " + time);
            } // if

            // данные для проверки подписи
            final Asn1BerEncodeBuffer encBufSignedAttr = new Asn1BerEncodeBuffer();
            info.signedAttrs.needSortSignedAttributes = needSortSignedAttributes;
            info.signedAttrs.encode(encBufSignedAttr);

            data = encBufSignedAttr.getMsgCopy();

        } // if

        // Проверяем подпись.
        Signature signature = providerName != null
                ? Signature.getInstance(signAlgOid, providerName)
                : Signature.getInstance(signAlgOid);

        signature.initVerify(cert);
        signature.update(data);

        boolean verified = signature.verify(sign);

        // Если подпись некорректна, но нас есть подписанные аттрибуты,
        // то пробуем проверить подпись также, отключив сортировку аттрибутов
        // перед кодированием в байтовый массив.
        if (!verified && info.signedAttrs != null && needSortSignedAttributes) {
            return verifyOnCert(cert, info, text, eContentTypeOID,
                    false, digestAlgOid, signAlgOid, providerName);
        } // if

        return verified;
    }

    /**
     * @param bytes bytes
     * @param digestAlgorithmName algorithm
     * @return digest
     * @throws Exception e
     */
    public static byte[] digestm(byte[] bytes, String digestAlgorithmName)
            throws Exception {
        return digestm(bytes, digestAlgorithmName, JCP.PROVIDER_NAME);
    }

    /**
     * @param bytes bytes
     * @param digestAlgorithmName algorithm
     * @param providerName provider name
     * @return digest
     * @throws Exception e
     */
    public static byte[] digestm(byte[] bytes, String digestAlgorithmName,
                                 String providerName) throws Exception {

        // calculation messageDigest
        final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        final java.security.MessageDigest digest = providerName != null
                ? java.security.MessageDigest.getInstance(digestAlgorithmName, providerName)
                : java.security.MessageDigest.getInstance(digestAlgorithmName);

        final DigestInputStream digestStream = new DigestInputStream(stream, digest);
        while (digestStream.available() != 0) digestStream.read();
        return digest.digest();
    }
}