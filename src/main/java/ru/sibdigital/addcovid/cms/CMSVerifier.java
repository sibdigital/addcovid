package ru.sibdigital.addcovid.cms;

import com.objsys.asn1j.runtime.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ru.sibdigital.addcovid.utils.CMS.digestm;

@Slf4j
public class CMSVerifier {

    public static final String STR_CMS_OID_DATA = "1.2.840.113549.1.7.1";
    public static final String STR_CMS_OID_SIGNED = "1.2.840.113549.1.7.2";

    public static final String STR_CMS_OID_CONT_TYP_ATTR = "1.2.840.113549.1.9.3";
    public static final String STR_CMS_OID_DIGEST_ATTR = "1.2.840.113549.1.9.4";
    public static final String STR_CMS_OID_SIGN_TYM_ATTR = "1.2.840.113549.1.9.5";

    private final static Logger verificationLog = LoggerFactory.getLogger("VerificationLogger");

    private List<Certificate> certificates = new ArrayList<>();
    private List<Certificate> rootCertificates = new ArrayList<>();
    private List<CertificateInfo> certificateInfos = new ArrayList<>();
    private Certificate rootCertificatesInPath = null;
    private String providerName = JCP.PROVIDER_NAME;
    private Date verifyDate = new Date();

    private SignedData cms;
    private String certDigestOid = null;
    private String signAlg       = null;
    private List<OID> digestOidList = new ArrayList<>();
    private OID digestOid = null;
    private OID eContTypeOID = null;
    private DigestAlgorithmIdentifier digestAlgorithmIdentifier = null;
    int validsign = 0;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private boolean dataPresent = false; // присуствуют подписанные данные
    private boolean signaturePresent = false; // присутствует подпись
    private boolean signedDataReadable = false; // подписанные данные прочитан
    private boolean messageDigestVerify = false; // проверка подписи завершилась успешно
//    private boolean isCertificateReadable = false;
    private boolean certificatePathBuild = false; // Цепочка сертификатов до корневого сертификата успешно построена
    private boolean certificatePathNotContainsRevocationCertificate = false; // цепочка сертификатов не содержит отозванных сертификатов из СОС
    //private boolean certificateSignaturesValid = false; //открытый ключ подписи прошел проверку - много случаев если не прошел
    private boolean algorithmSupported = false; //поддерживаемый алгоритм подписи ГОСТ 2021 256, 512
    private boolean allCerificateValid = false; //все сертификаты действуют на дату
    private boolean certificatePresent = false; //в файле подписи есть сертификаты

    private VerifiedData verifiedData;

    private boolean verifyCertPath() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException{
        boolean result = false;

        for (Certificate rootCert : getRootCertificates()){

            result = CertificatePathVerifier.verifyAll(rootCert, getCertificates());
            if (result){
                rootCertificatesInPath = rootCert;
                break;
            }
        }
        certificatePathBuild = result;
        certificatePathNotContainsRevocationCertificate = result;
        return result;
    }

    private void checkCertificateValid(){
        boolean result = true;
        boolean current = true;
        for (Certificate cert : getCertificates()){
            X509Certificate xcert  = (X509Certificate) cert;
            try {
                xcert.checkValidity(getVerifyDate());
                current = true;
            } catch (CertificateExpiredException | CertificateNotYetValidException ex) {
                current = false;
                CertificateInfo ci = new CertificateInfo(xcert);
                verificationLog.error("false validity: " + ci.toString() + " exception: " + ex.getMessage(), ex);
            }
            result &= current;
        }
        allCerificateValid = result;
    }

    public boolean verify(){

        verificationLog.info("start verify: " + verifiedData);
        dataPresent = !verifiedData.isEmptyData();
        signaturePresent = !verifiedData.isEmptySignature();
        if (!isDataPresent() || !isSignaturePresent()){
            verificationLog.info("empty data or signature");
            return false;
        }

        JCPInit.initProviders(false);

        boolean result = false;
        try {
            readCMS();
            signedDataReadable = true;

            readAlgorithms();
            algorithmSupported = true;

            if (cms.certificates != null) {
                final List<X509Certificate> cmsSerificates = getCMSSertificates(cms);
                certificatePresent = !cmsSerificates.isEmpty();
                getCertificates().clear();
                getCertificates().addAll(cmsSerificates);
                getCertificateInfos().addAll(cmsSerificates.stream()
                        .map(c -> new CertificateInfo(c))
                        .collect(Collectors.toList()));
            }

            try {
                processSignatures();//проверяем подпись
            }catch (CMSVerifyException ex){
                verificationLog.error(ex.getMessage(), ex);
            }catch (SignatureException ex) {
                verificationLog.error(ex.getMessage(), ex);
            }

            if (validsign == 0) {
                messageDigestVerify = false;
                verificationLog.error("Signatures are invalid");
            } else if (cms.signerInfos.elements.length > validsign) {
                messageDigestVerify = false;
                verificationLog.error("Some signatures are invalid");
            }else{
                messageDigestVerify = true;
                //certificateSignaturesValid = true;
            }

            checkCertificateValid(); //проверяем даты сертифкатов
            verifyCertPath(); //проверяем цепочку

            result = true;

        }catch (CMSVerifyException ex){
            verificationLog.error(ex.getMessage(), ex);
        }catch (Asn1Exception ex){
            verificationLog.error(ex.getMessage(), ex);
        }catch (IOException ex){
            verificationLog.error(ex.getMessage(), ex);
        } catch (CertificateException ex) {
            verificationLog.error(ex.getMessage(), ex);
        } catch (NoSuchAlgorithmException ex) {
            verificationLog.error(ex.getMessage(), ex);
        } catch (InvalidKeyException ex) {
            verificationLog.error(ex.getMessage(), ex);
        } catch (InvalidAlgorithmParameterException ex) {
            verificationLog.error(ex.getMessage(), ex);
        } catch (NoSuchProviderException ex) {
            verificationLog.error(ex.getMessage(), ex);
        }
        verificationLog.info("end verify: " + verifiedData + " results: " + createVerifyInfo());
        return result;
    }

    private void readCMS() throws CMSVerifyException, Asn1Exception, IOException {
        final byte[] buffer = getVerifiedData().getSignature();
        final Asn1BerDecodeBuffer asnBuf = new Asn1BerDecodeBuffer(buffer);
        final ContentInfo all = new ContentInfo();
        all.decode(asnBuf);

        if (!new OID(STR_CMS_OID_SIGNED).eq(all.contentType.value)) {
            throw new CMSVerifyException("Not supported ");
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

    private void processSignatures() throws CMSVerifyException, Asn1Exception, NoSuchAlgorithmException,
            CertificateEncodingException, SignatureException, NoSuchProviderException, InvalidKeyException, IOException {
        for (Certificate certElem : getCertificates()) {
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
                        }
                    }
                }
                if (expectedDigestOid == null) {
                    throw new CMSVerifyException("Отсутствует подпись в сертификате!");
                }

                SignatureAlgorithmIdentifier expectedSignId = info.signatureAlgorithm;
                String expectedSignAlg;

                if (signAlg == null) {
                    OID signAlgOid  = new OID(expectedSignId.algorithm.value);
                    expectedSignAlg = signAlgOid.toString();
                } else {
                    expectedSignAlg = signAlg;
                }

                expectedSignAlg = validateSignatureAlgorithm(expectedSignAlg);
                final boolean checkResult = verifyOnCert(cert, cms.signerInfos.elements[j], true, currentDigestOid, expectedSignAlg, providerName);

                if (checkResult) {
                    validsign++;
                    CertificateInfo ci = new CertificateInfo(cert);
                    verificationLog.warn("VALID signature on cert: " + ci.toString());
                }else{
                    CertificateInfo ci = new CertificateInfo(cert);
                    verificationLog.warn("invalid signature on cert: " + ci.toString());
                }

            }
        }
    }

    private List<X509Certificate> getCMSSertificates(SignedData cms) throws Asn1Exception, CertificateException {
        List<X509Certificate> list = new ArrayList<>();
        for (int i = 0; i < cms.certificates.elements.length; i++) {
            final Asn1BerEncodeBuffer encBuf = new Asn1BerEncodeBuffer();
            cms.certificates.elements[i].encode(encBuf);

            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final X509Certificate cert = (X509Certificate) cf.generateCertificate(encBuf.getInputStream());
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
    private boolean verifyOnCert(X509Certificate cert, SignerInfo info, boolean needSortSignedAttributes, OID digestAlgOid,
                                 String signAlgOid, String providerName)
            throws Asn1Exception, CertificateEncodingException, IOException, CMSVerifyException, NoSuchProviderException,
            NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        //см. rfc 5652 раздел 5.3
        byte[] text = verifiedData.getData();
        // подпись
        final byte[] sign = info.signature.value;

        // данные для проверки подписи
        final byte[] data;

        CertificateInfo ci = new CertificateInfo(cert);

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

                        verificationLog.error("Certificate stored in signing-certificateV2 is not equal to " + ci.toString());
                        return false;
                    } // if

                } // for

            } // if

            // проверка аттрибута content-type
            final Asn1ObjectIdentifier contentTypeOid = new Asn1ObjectIdentifier((new OID(STR_CMS_OID_CONT_TYP_ATTR)).value);
            Attribute contentTypeAttr = null;

            for (int r = 0; r < signAttrElem.length; r++) {
                final Asn1ObjectIdentifier oid = signAttrElem[r].type;
                if (oid.equals(contentTypeOid)) {
                    contentTypeAttr = signAttrElem[r];
                } // if
            } // for

            if (contentTypeAttr == null) {
                throw new CMSVerifyException("content-type attribute not present on cert \n" + ci.toString());
            } // if

            if (!contentTypeAttr.values.elements[0].equals(new Asn1ObjectIdentifier(eContTypeOID.value))) {
                throw new CMSVerifyException("content-type attribute OID not equal eContentType OID on cert \n" + ci.toString());
            } // if

            // проверка аттрибута message-digest
            final Asn1ObjectIdentifier messageDigestOid = new Asn1ObjectIdentifier((new OID(STR_CMS_OID_DIGEST_ATTR)).value);

            Attribute messageDigestAttr = null;

            for (int r = 0; r < signAttrElem.length; r++) {
                final Asn1ObjectIdentifier oid = signAttrElem[r].type;
                if (oid.equals(messageDigestOid)) {
                    messageDigestAttr = signAttrElem[r];
                } // if
            } // for

            if (messageDigestAttr == null) {
                throw new CMSVerifyException("message-digest attribute not present on cert \n" + ci.toString());
            } // if

            final Asn1Type open = messageDigestAttr.values.elements[0];
            final Asn1OctetString hash = (Asn1OctetString) open;
            final byte[] md = hash.value;

            // вычисление messageDigest
            final byte[] dm = digestm(text, digestAlgOid.toString(), providerName);

            if (!Array.toHexString(dm).equals(Array.toHexString(md))) {
                throw new CMSVerifyException("message-digest attribute verify failed on cert \n" + ci.toString());
            } // if

            // проверка аттрибута signing-time
            final Asn1ObjectIdentifier signTimeOid = new Asn1ObjectIdentifier((new OID(STR_CMS_OID_SIGN_TYM_ATTR)).value);

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
                if (time != null){
                    final Calendar cldr = time.getTime();
                    verificationLog.warn("Signing Time: " + dateFormat.format(cldr.getTime()));
                }else {
                    verificationLog.warn("Signing Time: " + time);
                }
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
    public static byte[] digestm(byte[] bytes, String digestAlgorithmName, String providerName) throws NoSuchProviderException, NoSuchAlgorithmException, IOException {

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

    private String createVerifyInfo(){
        String result = "";
        result += " isAlgorithmSupported=" + isAlgorithmSupported();
        result += " isAllCerificateValid=" +  isAllCerificateValid();
        result += " isMessageDigestVerify=" +  isMessageDigestVerify();
        result += " isCertificatePathBuild=" +  isCertificatePathBuild();
        result += " isCertificatePathNotContainsRevocationCertificate=" +  isCertificatePathNotContainsRevocationCertificate();
        result += " isDataPresent=" +  isDataPresent();
        result += " isSignaturePresent=" +  isSignaturePresent();
        result += " isSignedDataReadable=" +  isSignedDataReadable();
        result += " isCertificatePresent=" +  isCertificatePresent();
        return result;
    }


    public List<Certificate> getRootCertificates() {
        return rootCertificates;
    }

    public void setRootCertificates(List<Certificate> rootCertificates) {
        this.rootCertificates = rootCertificates;
    }

    public Date getVerifyDate() {
        return verifyDate;
    }

    public void setVerifyDate(Date verifyDate) {
        this.verifyDate = verifyDate;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public List<CertificateInfo> getCertificateInfos() {
        return certificateInfos;
    }

    public boolean isDataPresent() {
        return dataPresent;
    }

    public boolean isSignaturePresent() {
        return signaturePresent;
    }

    public boolean isSignedDataReadable() {
        return signedDataReadable;
    }

    public boolean isMessageDigestVerify() {
        return messageDigestVerify;
    }

    public boolean isCertificatePathBuild() {
        return certificatePathBuild;
    }

    public boolean isCertificatePathNotContainsRevocationCertificate() {
        return certificatePathNotContainsRevocationCertificate;
    }

    public boolean isAlgorithmSupported() {
        return algorithmSupported;
    }

    public boolean isAllCerificateValid() {
        return allCerificateValid;
    }

    public boolean isCertificatePresent() {
        return certificatePresent;
    }
}
