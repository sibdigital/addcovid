package ru.sibdigital.addcovid.utils;

import com.objsys.asn1j.runtime.*;
import lombok.extern.slf4j.Slf4j;
import ru.CryptoPro.JCP.ASN.CertificateExtensions.GeneralName;
import ru.CryptoPro.JCP.ASN.CertificateExtensions.GeneralNames;
import ru.CryptoPro.JCP.ASN.CryptographicMessageSyntax.*;
import ru.CryptoPro.JCP.ASN.PKIX1Explicit88.*;
//import ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Certificate;
import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.Util.JCPInit;
import ru.CryptoPro.JCP.params.OID;
import ru.CryptoPro.JCP.tools.Array;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.*;

import static ru.sibdigital.addcovid.utils.CMS.digestm;

@Slf4j
public class CMSVerifier {

    public static final String STR_CMS_OID_DATA = "1.2.840.113549.1.7.1";
    public static final String STR_CMS_OID_SIGNED = "1.2.840.113549.1.7.2";

    public static final String STR_CMS_OID_CONT_TYP_ATTR = "1.2.840.113549.1.9.3";
    public static final String STR_CMS_OID_DIGEST_ATTR = "1.2.840.113549.1.9.4";
    public static final String STR_CMS_OID_SIGN_TYM_ATTR = "1.2.840.113549.1.9.5";

    private List<Certificate> certificates = new ArrayList<>();
    private String providerName = JCP.PROVIDER_NAME;

    private SignedData cms;
    private String certDigestOid = null;
    private String signAlg       = null;
    private List<OID> digestOidList = new ArrayList<>();
    private OID digestOid = null;
    private OID eContTypeOID = null;
    private DigestAlgorithmIdentifier digestAlgorithmIdentifier = null;
    int validsign = 0;

    private List<String> errors = new ArrayList<>();

    private List<String> addError(String error){
        errors.add(error);
        return errors;
    }

    private VerifiedData verifiedData;

    public void checkCertPath() throws FileNotFoundException, CertificateException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CertPathBuilderException, CertPathValidatorException {

        String rootCertPAth = "/home/bulat/IdeaProjects/mvn_project/addcovid/libs/cryptopro/samples-sources/guts_2012.cer";
        String failedCertPath = "/home/bulat/IdeaProjects/mvn_project/addcovid/libs/cryptopro/samples-sources/sibd2019_12_17der.cer";
        //String failedCertPath = "/home/bulat/IdeaProjects/mvn_project/addcovid/libs/cryptopro/samples-sources/sibd2021_12_10der.cer";


        System.setProperty("com.sun.security.enableCRLDP", "true"); // для проверки по CRL DP
        System.setProperty("com.sun.security.enableAIAcaIssuers", "true"); // для загрузки сертификатов по AIA из сети
        System.setProperty("ru.CryptoPro.reprov.enableAIAcaIssuers", "true"); // для загрузки сертификатов по AIA из сети

        JCPInit.initProviders(false);
        final CertificateFactory cf = CertificateFactory.getInstance("X509");

        final Certificate user = certificates.get(0);//cf.generateCertificate(new FileInputStream(PATH + "user.cer"));
        Certificate root = cf.generateCertificate(new FileInputStream(rootCertPAth));
        Certificate failedCert = cf.generateCertificate(new FileInputStream(failedCertPath));

        final Certificate[] certs = new Certificate[2];
        certs[0] = failedCert;//user;
        certs[1] = root;

        final Set<TrustAnchor> trust = new HashSet<TrustAnchor>(1);
        trust.add(new TrustAnchor((X509Certificate) root, null));

        final List cert = new ArrayList(0);
        for (int i = 0; i < certs.length; i++) {
            cert.add(certs[i]);
        }

        final PKIXBuilderParameters cpp = new PKIXBuilderParameters(trust, null);
        cpp.setSigProvider(null);

        final CollectionCertStoreParameters par = new CollectionCertStoreParameters(cert);

        final CertStore store = CertStore.getInstance("Collection", par);
        cpp.addCertStore(store);

        final X509CertSelector selector = new X509CertSelector();
        selector.setCertificate((X509Certificate) user);

        cpp.setTargetCertConstraints(selector);
        cpp.setRevocationEnabled(false);

        // Построение цепочки.

        final PKIXCertPathBuilderResult res = (PKIXCertPathBuilderResult) CertPathBuilder.
                        getInstance("CPPKIX", "RevCheck").build(cpp);

        final CertPath cp = res.getCertPath();

        System.out.println("%%% SIZE: " + cp.getCertificates().size());
        //System.out.println("%%% PATH:\n" + cp);
        System.out.println("OK-1");

        // Проверка цепочки.

        final CertPathValidator cpv = CertPathValidator.getInstance("CPPKIX", "RevCheck");
        cpp.setRevocationEnabled(true);

        final CertPathValidatorResult validate = cpv.validate(cp, cpp);
        System.out.println("OK-2");
        //System.out.println(validate);

    }

    public boolean verify(){
        JCPInit.initProviders(false);
        //Security.addProvider(new CryptoProvider()); //<--
        //Security.addProvider(new RevCheck());

        Provider[] sp = Security.getProviders();
        for(Provider i : sp){
            //log.warn(i.getName());
        }
        boolean result = false;
        try {
            readCMS();

            readAlgorithms();

            if (cms.certificates != null) {
                final List<X509Certificate> cmsSerificates = getCMSSerificates(cms);
                certificates.clear();
                certificates.addAll(cmsSerificates);
            }

            processCertificates();

            if (validsign == 0) {
                throw new CMSVerifyException("Signatures are invalid: ");
            } // if

            if (cms.signerInfos.elements.length > validsign) {
                throw new CMSVerifyException("Some signatures are invalid: ");
            }

            result = true;

        }catch (CMSVerifyException ex){
            log.error(ex.getMessage(), ex);
            addError(ex.getMessage());
        }catch (Asn1Exception ex){
            log.error(ex.getMessage(), ex);
            addError("Невозможно прочитать подпись!");
        }catch (IOException ex){
            log.error(ex.getMessage(), ex);
            addError("Невозможно прочитать подпись!");
        } catch (CertificateException ex) {
            log.error(ex.getMessage(), ex);
            addError("Невозможно прочитать сертификат!");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            addError("Неизвестная ошибка!");
        }
        return result;
    }

    private void readCMS() throws CMSVerifyException, Asn1Exception, IOException {
        final byte[] buffer = getVerifiedData().getSignature();
        final Asn1BerDecodeBuffer asnBuf = new Asn1BerDecodeBuffer(buffer);
        final ContentInfo all = new ContentInfo();
        all.decode(asnBuf);

        if (!new OID(STR_CMS_OID_SIGNED).eq(all.contentType.value)) {
            throw new CMSVerifyException("Not supported");
        } // if

        cms = (SignedData) all.content;
    }

    private void readAlgorithms() throws CMSVerifyException{
        for (int i = 0; i < cms.digestAlgorithms.elements.length; i++) {

            OID currentOid = new OID(cms.digestAlgorithms.elements[i].algorithm.value);
            if (digestAlgorithmIdentifier != null &&
                    cms.digestAlgorithms.elements[i].algorithm.equals(digestAlgorithmIdentifier.algorithm)) {
                digestOid = currentOid;
                break; // сразу нашли
            } else {
                digestOidList.add(currentOid);
            }
        }

        if (digestOid == null && digestOidList.isEmpty()) {
            throw new CMSVerifyException("Неподдерживаемый алгоритм подписи!");
        }
        eContTypeOID = new OID(cms.encapContentInfo.eContentType.value);
    }

    private void processCertificates() throws Exception {
        for (Certificate certElem : certificates) {
            final X509Certificate cert = (X509Certificate) certElem;
            for (int j = 0; j < cms.signerInfos.elements.length; j++) {

                final SignerInfo info = cms.signerInfos.elements[j];
                final OID currentDigestOid = new OID(info.digestAlgorithm.algorithm.value);
                OID expectedDigestOid = null;

                if (digestOid != null) {
                    expectedDigestOid = digestOid;
                } else {
                    for (int n = 0; n < digestOidList.size(); n++) {
                        if (digestOidList.get(n).equals(currentDigestOid)) {
                            expectedDigestOid = currentDigestOid;
                            break;
                        } // if
                    }
                }
                if (expectedDigestOid == null) {
                    throw new CMSVerifyException("Not signed on certificate.");
                } // if

                SignatureAlgorithmIdentifier expectedSignId = info.signatureAlgorithm;
                String expectedSignAlg;

                if (signAlg == null) {
                    OID signAlgOid  = new OID(expectedSignId.algorithm.value);
                    expectedSignAlg = signAlgOid.toString();
                } else {
                    expectedSignAlg = signAlg;
                }

                expectedSignAlg = validateSignatureAlgorithm(expectedSignAlg);
                final boolean checkResult = verifyOnCert(cert, cms.signerInfos.elements[j],
                        true, currentDigestOid, expectedSignAlg, providerName);

                if (checkResult) {
                    validsign++;
                } // if

            } // for

        }
    }

    private List<X509Certificate> getCMSSerificates(SignedData cms) throws Asn1Exception, CertificateException {
        List<X509Certificate> list = new ArrayList<>();
        for (int i = 0; i < cms.certificates.elements.length; i++) {
            final Asn1BerEncodeBuffer encBuf = new Asn1BerEncodeBuffer();
            cms.certificates.elements[i].encode(encBuf);

            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final X509Certificate cert = (X509Certificate) cf
                    .generateCertificate(encBuf.getInputStream());
            list.add(cert);
        }
        return list;
    }

    public VerifiedData getVerifiedData() {
        return verifiedData;
    }

    public void setVerifiedData(VerifiedData verifiedData) {
        this.verifiedData = verifiedData;
    }


    /**
     * Попытка проверки подписи на указанном сертификате.
     * Проверка может быть выполнена как по отсортированным
     * подписанным аттрибутам, так и по несортированным.
     *
     * @param cert сертификат для проверки
     * //@param text текст для проверки
     * @param info подпись
     * //@param eContentTypeOID тип содержимого
     * @param needSortSignedAttributes True, если необходимо проверить
     * подпись по отсортированным подписанным аттрибутам. По умолчанию
     * подписанные аттрибуты сортируются перед кодированием.
     * @param digestAlgOid Алгоритм хеширования.
     * @param signAlgOid Алгоритм подписи.
     * @param providerName Имя провайдера.
     * @return верна ли подпись
     * @throws Exception ошибки
     */
    private boolean verifyOnCert(X509Certificate cert, SignerInfo info,
                           boolean needSortSignedAttributes, OID digestAlgOid, String signAlgOid, String providerName)
            throws Exception {
        byte[] text = verifiedData.getData();
        // подпись
        final byte[] sign = info.signature.value;

        // данные для проверки подписи
        final byte[] data;

        if (info.signedAttrs == null) {
            // аттрибуты подписи не присутствуют
            // данные для проверки подписи
            data = text;
        }else{

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

                    ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Certificate certificate = new ru.CryptoPro.JCP.ASN.PKIX1Explicit88.Certificate();
                    Asn1BerDecodeBuffer decodeBuffer = new Asn1BerDecodeBuffer(cert.getEncoded());
                    certificate.decode(decodeBuffer);

                    GeneralName[] issuerName = new GeneralName[1];
                    issuerName[0] = new GeneralName(GeneralName._DIRECTORYNAME, certificate.tbsCertificate.issuer);
                    GeneralNames issuerNames = new GeneralNames(issuerName);

                    IssuerSerial actualIssuerSerial = new IssuerSerial(issuerNames, certificate.tbsCertificate.serialNumber);
                    Asn1BerEncodeBuffer encodedActualIssuerSerial = new Asn1BerEncodeBuffer();
                    actualIssuerSerial.encode(encodedActualIssuerSerial);

                    if ( !(Arrays.equals(actualCertHash.value, expectedCertHash.value) &&
                            Arrays.equals(encodedActualIssuerSerial.getMsgCopy(), encodedActualIssuerSerial.getMsgCopy())) ) {

                        addError("Certificate stored in signing-certificateV2 is not equal to " + cert.getSubjectDN());
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
                throw new CMSVerifyException("content-type attribute not present");
            } // if

            if (!contentTypeAttr.values.elements[0]
                    .equals(new Asn1ObjectIdentifier(eContTypeOID.value))) {
                throw new CMSVerifyException("content-type attribute OID not equal eContentType OID");
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
                throw new CMSVerifyException("message-digest attribute not present");
            } // if

            final Asn1Type open = messageDigestAttr.values.elements[0];
            final Asn1OctetString hash = (Asn1OctetString) open;
            final byte[] md = hash.value;

            // вычисление messageDigest
            final byte[] dm = digestm(text, digestAlgOid.toString(), providerName);

            if (!Array.toHexString(dm).equals(Array.toHexString(md))) {
                throw new CMSVerifyException("message-digest attribute verify failed");
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
                log.warn("Signing Time: " + time);
            } // if

            // данные для проверки подписи
            final Asn1BerEncodeBuffer encBufSignedAttr = new Asn1BerEncodeBuffer();
            info.signedAttrs.needSortSignedAttributes = needSortSignedAttributes;
            info.signedAttrs.encode(encBufSignedAttr);

            data = encBufSignedAttr.getMsgCopy();

        } // if

        // Проверяем подпись.
        java.security.Signature signature = providerName != null
                ? java.security.Signature.getInstance(signAlgOid, providerName)
                : java.security.Signature.getInstance(signAlgOid);

        signature.initVerify(cert);
        signature.update(data);

        boolean verified = signature.verify(sign);

        // Если подпись некорректна, но нас есть подписанные аттрибуты,
        // то пробуем проверить подпись также, отключив сортировку аттрибутов
        // перед кодированием в байтовый массив.
        if (!verified && info.signedAttrs != null && needSortSignedAttributes) {
            return verifyOnCert(cert, info, false, digestAlgOid, signAlgOid, providerName);
        } // if

        return verified;
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


}
