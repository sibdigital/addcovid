package ru.sibdigital.addcovid.cms;

import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Date;

public class CMSTest {

    public static void main(String[] args) throws Exception {

        String rootCertPAth = "/home/bulat/IdeaProjects/mvn_project/addcovid/libs/cryptopro/samples-sources/guts_2012.cer";
        String rootCertPAth2 = "/home/bulat/IdeaProjects/mvn_project/addcovid/libs/cryptopro/samples-sources/CA_FNS_Russia_2017.crt";

        final CertificateFactory cf = CertificateFactory.getInstance("X509");
        Certificate root = cf.generateCertificate(new FileInputStream(rootCertPAth));
        Certificate root2 = cf.generateCertificate(new FileInputStream(rootCertPAth2));

        CMSVerifier cmsVerifier = new CMSVerifier();
        cmsVerifier.getRootCertificates().add(root);
        cmsVerifier.getRootCertificates().add(root2);

//        VerifiedData verifiedData =  new VerifiedData(
//                "/home/bulat/pkcs7/Заявление_№351573-Заявление о включении сведений в единый реестр.pdf.p7s",
//                "/home/bulat/pkcs7/Заявление_№351573-Заявление о включении сведений в единый реестр.pdf"
//            );

           VerifiedData verifiedData =  new VerifiedData(
                "/home/bulat/pkcs7/inn.sgn",
                "/home/bulat/pkcs7/inn.PDF"
            );

//        VerifiedData verifiedData =  new VerifiedData(
//                "/home/bulat/pkcs7/test.test",
//                "/home/bulat/pkcs7/Заявление_№351573-Заявление о включении сведений в единый реестр.pdf"
//        );

//        VerifiedData verifiedData =  new VerifiedData(
//                "/home/bulat/pkcs7/response_2017-2-3_11-13-14_688.zip.p7s",
//                "/home/bulat/pkcs7/response_2017-2-3_11-13-14_688.zip"
//        );

//        VerifiedData verifiedData = new VerifiedData(
//                "/home/bulat/pkcs7/Заявление_№351573-Заявление о включении сведений в единый реестр.pdf.p7s",
//                "/home/bulat/pkcs7/test.test"
//        );
        System.out.println("begin: " + new Date());
        cmsVerifier.setVerifiedData(verifiedData);
        cmsVerifier.verify();
        //cmsVerifier.checkCertPath();
        System.out.println("end: " + new Date());
    }
}
