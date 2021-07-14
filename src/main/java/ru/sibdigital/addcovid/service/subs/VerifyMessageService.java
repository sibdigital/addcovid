package ru.sibdigital.addcovid.service.subs;

import ru.sibdigital.addcovid.cms.CMSVerifier;

public interface VerifyMessageService {

    String getVerifyResult(CMSVerifier cmsVerifier);

}
