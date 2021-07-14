package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.addcovid.cms.CMSVerifier;
import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.sibdigital.addcovid.model.ClsDepartment;
import ru.sibdigital.addcovid.model.ClsDepartmentContact;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class TestController {

    @GetMapping("/test_verify_process")
    @ResponseBody
    public String testVerifyProcess() {
        String rootCertPAth = "/home/guts_2012.cer";
        String result = "";
        final CertificateFactory cf;
        CMSVerifier cmsVerifier = new CMSVerifier();
        try {
            cf = CertificateFactory.getInstance("X509");
            Certificate root = cf.generateCertificate(new FileInputStream(rootCertPAth));
            cmsVerifier.getRootCertificates().add(root);

            VerifiedData verifiedData = new VerifiedData(
                 "/home/test_verify.pdf.p7s",
                 "/home/test_verify.pdf"
            );
            cmsVerifier.setVerifiedData(verifiedData);
            cmsVerifier.verify();

        } catch (CertificateException | FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        result += " isAlgorithmSupported=" + cmsVerifier.isAlgorithmSupported();
        result += "\n isAllCerificateValid=" +  cmsVerifier.isAllCerificateValid();
        result += "\n isMessageDigestVerify=" +  cmsVerifier.isMessageDigestVerify();
        result += "\n isCertificatePathBuild=" +  cmsVerifier.isCertificatePathBuild();
        result += "\n isCertificatePathNotContainsRevocationCertificate=" +  cmsVerifier.isCertificatePathNotContainsRevocationCertificate();
        result += "\n isDataPresent=" +  cmsVerifier.isDataPresent();
        result += "\n isSignaturePresent=" +  cmsVerifier.isSignaturePresent();
        result += "\n isSignedDataReadable=" +  cmsVerifier.isSignedDataReadable();
        result += "\n isCertificatePresent=" +  cmsVerifier.isCertificatePresent();
        result += "\n" + cmsVerifier.getCertificateInfos()
                .stream().map(ci -> ci.toString() + "\n").reduce((s1, s2) -> s1 + s2);
        return result;
    }
}
