package ru.sibdigital.addcovid.cms;

import java.util.Date;

public class CMSTest {

    public static void main(String[] args) throws Exception {

        CMSVerifier cmsVerifier =  new CMSVerifier();

        VerifiedData verifiedData =  new VerifiedData(
                "/home/bulat/pkcs7/Заявление_№351573-Заявление о включении сведений в единый реестр.pdf.p7s",
                "/home/bulat/pkcs7/Заявление_№351573-Заявление о включении сведений в единый реестр.pdf"
            );

//        VerifiedData verifiedData =  new VerifiedData(
//                "/home/bulat/pkcs7/response_2017-2-3_11-13-14_688.zip.p7s",
//                "/home/bulat/pkcs7/response_2017-2-3_11-13-14_688.zip"
//        );

//                VerifiedData verifiedData =  new VerifiedData(
//                "/home/bulat/pkcs7/Заявление_№351573-Заявление о включении сведений в единый реестр.pdf.p7s",
//                "/home/bulat/pkcs7/test.test"
//            );
        System.out.println("begin: " + new Date());
        cmsVerifier.setVerifiedData(verifiedData);
        cmsVerifier.verify();
        //cmsVerifier.checkCertPath();
        System.out.println("end: " + new Date());
    }
}
