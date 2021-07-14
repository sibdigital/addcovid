package ru.sibdigital.addcovid.queue;

import lombok.extern.slf4j.Slf4j;
import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;

import javax.annotation.Nullable;

@Slf4j
public class VerifyTaskPayloadTransformer implements TaskPayloadTransformer<VerifiedData> {

    @Nullable
    @Override
    public VerifiedData toObject(@Nullable String s) {
        final String[] split = s.split("-");
        if (split.length == 3) {
            String idRequestSubsidyFile = split[0];
            String idRequestSubsidyFileSignature = split[1];
            String idRequest = split[2];
            VerifiedData verifiedData = new VerifiedData("", "");
            verifiedData.setIdentificator(idRequestSubsidyFile);
            verifiedData.setSignatureIdentificator(idRequestSubsidyFileSignature);
            verifiedData.setGroup(idRequest);
            return verifiedData;
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public String fromObject(@Nullable VerifiedData verifiedData) {
        String id = "";
        id = String.format("%s-%s-%s", verifiedData.getIdentificator(), verifiedData.getSignatureIdentificator(),
                verifiedData.getGroup());
        return id;
    }
}
