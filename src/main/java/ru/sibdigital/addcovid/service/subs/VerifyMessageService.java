package ru.sibdigital.addcovid.service.subs;

import ru.sibdigital.addcovid.cms.CMSVerifier;
import ru.sibdigital.addcovid.cms.VerifiedData;

import java.security.cert.Certificate;
import java.util.List;

public interface VerifyMessageService {

    String getVerifyResult(CMSVerifier cmsVerifier);

    List<Certificate> getAnchorCertificates();

    CMSVerifier buildCMSVerifier(VerifiedData verifiedData);

}
