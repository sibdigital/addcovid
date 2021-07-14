package ru.sibdigital.addcovid.service.subs;

import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.cms.CMSVerifier;
import ru.sibdigital.addcovid.cms.CertificateInfo;
import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.sibdigital.addcovid.model.ClsSettings;
import ru.sibdigital.addcovid.repository.ClsSettingsRepo;
import org.springframework.beans.factory.annotation.Value;
import ru.sibdigital.addcovid.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class VerifyMessageServiceImpl implements VerifyMessageService{

    private final static Logger verificationLog = LoggerFactory.getLogger("VerificationLogger");
    private static final String prefix = "isNo";

    @Value("${cms.certificate.root-catalog}")
    private String rootCertificateCatalog;

    @Autowired
    private ClsSettingsRepo settingsRepo;

    @Override
    public String getVerifyResult(CMSVerifier cmsVerifier) {

        String status = "<b>" + (cmsVerifier.isSuccess() ? "Подлинность документа ПОДТВЕРЖДЕНА"
                : "Подлинность документа НЕ ПОДТВЕРЖДЕНА") + "</b>";

        String result = status + "<br/>";
        String digestStatus = getSignatureInfo(cmsVerifier);
        result += digestStatus + "<br/>";

        String certStatus =  getCertificateInfo(cmsVerifier);
        result += certStatus + "<br/>";
        verificationLog.info(cmsVerifier.getVerifiedData() + "\n" + result);

        return result;
    }

    @Override
    public List<Certificate> getAnchorCertificates() {
        final CertificateFactory cf;
        List<Certificate> certificates = new ArrayList<>();
        try {
            cf = CertificateFactory.getInstance("X509");
            final List<File> files = FileUtils.getAllInDirectory(rootCertificateCatalog);
            for (File f : files){
                try {
                    final Certificate certificate = cf.generateCertificate(new FileInputStream(f));
                    certificates.add(certificate);
                } catch (FileNotFoundException ex) {
                    verificationLog.error(ex.getMessage(), ex);
                    log.error(ex.getMessage(), ex);
                };
            }
        } catch (CertificateException ex) {
            verificationLog.error(ex.getMessage(), ex);
            log.error(ex.getMessage(), ex);
        }
        //verificationLog.info("корневых сертифкатов: " + certificates.size());
        return certificates;
    }

    @Override
    public CMSVerifier buildCMSVerifier(VerifiedData verifiedData) {
        CMSVerifier cmsVerifier = new CMSVerifier();
        final List<Certificate> anchorCertificates = getAnchorCertificates();
        cmsVerifier.setRootCertificates(anchorCertificates);
        cmsVerifier.setVerifiedData(verifiedData);
        return cmsVerifier;
    }

    private String getSignatureInfo(CMSVerifier cmsVerifier){
        String digestStatus = "<b>Электронная подпись: </b>";
        //String result = status + "<br/>";
        if (cmsVerifier.signatureSuccessVerify()){
            digestStatus += "ВЕРНА";
        }else{
            if (cmsVerifier.isSignedDataReadable()) {
                String digestStatusDetail = getDigestStatusDetail(cmsVerifier);
                digestStatus += "НЕ ВЕРНА<br/>";
                digestStatus += "<b>Проверка электронной подписи показала:</b> " + digestStatusDetail;
            }else{

                digestStatus += "НЕ ВЕРНА<br/>";
                digestStatus += "<b>Проверка электронной подписи показала:</b> " +
                        getMessageOnSettings(!cmsVerifier.isSignedDataReadable(), prefix + "signedDataReadable",
                                "Входные данные не являются подписанным сообщением<br/>");
            }
        }
        return digestStatus;
    }

    private String getCertificateInfo(CMSVerifier cmsVerifier){
        String certStatus = "<b>Статус сертификата подписи: </b>";
        if (cmsVerifier.isSignedDataReadable()) {
            if (cmsVerifier.certificateSuccessVerify()) {
                certStatus += " ДЕЙСТВИТЕЛЕН, сертификат выдан аккредитованным удостоверяющим центром";
            } else {
                certStatus += getCertificateStatusDetail(cmsVerifier);
            }
            String certPathInfo = cmsVerifier.getCertificateInfos()
                    .stream().map(CertificateInfo::toHtmlString)
                    .reduce((ci1, ci2) -> ci1 + "<br/>" + ci2)
                    .orElse("Информация о сертификатах отсутствует"); //Подлинность документа не подтверждена
            certStatus += "<br/>" + certPathInfo;
        }else{
            certStatus += "<b>Проверка сертификата не проводилась.</b><br/>";
        }
        return certStatus;
    }

    private String getDigestStatusDetail(CMSVerifier cmsVerifier){
        String digestStatusDetail = "";
        String defaultDetail = "Электронная подпись не прошла проверку<br/>";

        digestStatusDetail += getMessageOnSettings(!cmsVerifier.isDataPresent(), prefix + "dataPresent", defaultDetail);
        digestStatusDetail += getMessageOnSettings(!cmsVerifier.isSignedDataReadable(), prefix + "signedDataReadable", defaultDetail);
        digestStatusDetail += getMessageOnSettings(!cmsVerifier.isAlgorithmSupported(), prefix + "algorithmSupported", defaultDetail);
        digestStatusDetail += getMessageOnSettings(!cmsVerifier.isSignaturePresent(), prefix + "signaturePresent", defaultDetail);
        digestStatusDetail += getMessageOnSettings(!cmsVerifier.isMessageDigestVerify(), prefix + "messageDigestVerify", defaultDetail);

        return digestStatusDetail;
    }

    private String getCertificateStatusDetail(CMSVerifier cmsVerifier){
        String digestStatusDetail = "";
        String defaultDetail = "Сертификат электронной подписи не прошел проверку<br/>";

        digestStatusDetail += getMessageOnSettings(!cmsVerifier.isSignaturePresent(), prefix + "signaturePresent", defaultDetail);
        digestStatusDetail += getMessageOnSettings(!cmsVerifier.isSignedDataReadable(), prefix + "signedDataReadable", defaultDetail);
        digestStatusDetail += getMessageOnSettings(!cmsVerifier.isCertificatePathBuild(), prefix + "certificatePathBuild", defaultDetail);
        digestStatusDetail += getMessageOnSettings(!cmsVerifier.isCertificatePathNotContainsRevocationCertificate(), prefix + "certificatePathNotContainsRevocationCertificate", defaultDetail);
        digestStatusDetail += getMessageOnSettings(!cmsVerifier.isAllCerificateValid(), prefix + "allCerificateValid", defaultDetail);
        digestStatusDetail += getMessageOnSettings(!cmsVerifier.isCertificatePresent(), prefix + "certificatePresent", defaultDetail);

        return digestStatusDetail;
    }

    private String getMessageOnSettings(boolean isAdd, String key, String defaultDetail){
        String msg = "";
        if (isAdd) {
            final Optional<ClsSettings> stn = settingsRepo.getActualByKey(key);
            msg = defaultDetail;
            if (stn.isPresent()) {
                msg = stn.get().getStringValue() + "<br/>";
            }
        }
        return msg;
    }
}
