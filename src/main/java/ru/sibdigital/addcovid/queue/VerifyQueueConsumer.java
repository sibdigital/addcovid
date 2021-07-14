package ru.sibdigital.addcovid.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sibdigital.addcovid.cms.CMSVerifier;
import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.addcovid.model.subs.RegVerificationSignatureFile;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.addcovid.repository.subs.DocRequestSubsidyRepo;
import ru.sibdigital.addcovid.repository.subs.RegVerificationSignatureFileRepo;
import ru.sibdigital.addcovid.repository.subs.TpRequestSubsidyFileRepo;
import ru.sibdigital.addcovid.service.subs.VerifyMessageService;
import ru.yoomoney.tech.dbqueue.api.*;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;

public class VerifyQueueConsumer implements QueueConsumer<VerifiedData> {

    @Autowired
    private TpRequestSubsidyFileRepo tpRequestSubsidyFileRepo;

    @Autowired
    private RegVerificationSignatureFileRepo regVerificationSignatureFileRepo;

    @Autowired
    private DocRequestSubsidyRepo docRequestSubsidyRepo;

    @Autowired
    private VerifyMessageService verifyMessageService;

    private final static Logger verificationLog = LoggerFactory.getLogger("VerificationLogger");

    private final QueueConfig queueConfig;

    public VerifyQueueConsumer(QueueConfig queueConfig) {
        this.queueConfig = queueConfig;
    }

    @Override
    public TaskExecutionResult execute(Task<VerifiedData> task) {
        try {
            VerifiedData verifiedData = task.getPayloadOrThrow();
            if (verifiedData != null) {
                Long idRequestSubsidyFile = Long.valueOf(verifiedData.getIdentificator());
                Long idRequestSubsidyFileSignature = Long.valueOf(verifiedData.getSignatureIdentificator());
                Long idRequestSubsidy = Long.valueOf(verifiedData.getGroup());
//                TpRequestSubsidyFile requestSubsidyFile = tpRequestSubsidyFileRepo.findById(idRequestSubsidyFile).orElse(null);
//                TpRequestSubsidyFile requestSubsidyFileSignature = tpRequestSubsidyFileRepo.findByIdAndRequestSubsidyFile_IdAndIsSignature(idRequestSubsidyFileSignature,
//                                                                    idRequestSubsidy, true);
//                DocRequestSubsidy docRequestSubsidy = docRequestSubsidyRepo.findById(idRequestSubsidy).orElse(null);

                RegVerificationSignatureFile rvsf =
                        regVerificationSignatureFileRepo.findByRequestSubsidy_IdAndRequestSubsidyFile_IdAndRequestSubsidySubsidySignatureFile_Id(idRequestSubsidy,
                                                                                    idRequestSubsidyFile, idRequestSubsidyFileSignature);
                CMSVerifier cmsVerifier = new CMSVerifier();
                rvsf.setVerifyResult(verifyMessageService.getVerifyResult(cmsVerifier));
                regVerificationSignatureFileRepo.save(rvsf);
            } else {
                verificationLog.error("Не удалось обработать: " + task.toString());
            }
        } catch (Exception e) {
            verificationLog.error("Не удалось обработать: " + task.toString());
        }

//        String idRequestSubsidyFile = split[0];
//        String idRequestSubsidyFileSignature = split[1];
//        String idRequest = split[2];
//
//        tpRequestSubsidyFileRepo.findById(idRequestSubsidyFile);
//        tpRequestSubsidyFileRepo.findById(idRequestSubsidyFileSignature); //id_subsidy_request_file = idRequestSubsidyFile;

        return TaskExecutionResult.finish();
    }

    @Override
    public QueueConfig getQueueConfig() {
        return queueConfig;
    }

    @Override
    public TaskPayloadTransformer<VerifiedData> getPayloadTransformer() {
        return new VerifyTaskPayloadTransformer();
    }
}
