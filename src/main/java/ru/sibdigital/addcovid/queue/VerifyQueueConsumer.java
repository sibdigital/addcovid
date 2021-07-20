package ru.sibdigital.addcovid.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.File;
import java.nio.file.Paths;
import java.sql.Timestamp;

public class VerifyQueueConsumer implements QueueConsumer<VerifiedData> {

    @Autowired
    private TpRequestSubsidyFileRepo tpRequestSubsidyFileRepo;

    @Autowired
    private RegVerificationSignatureFileRepo regVerificationSignatureFileRepo;

    @Autowired
    private DocRequestSubsidyRepo docRequestSubsidyRepo;

    @Autowired
    private VerifyMessageService verifyMessageService;

    @Value("${upload.path:/uploads}")
    String uploadingAttachmentDir;

    private final static Logger verificationLog = LoggerFactory.getLogger("VerificationLogger");

    private final QueueConfig queueConfig;

    public VerifyQueueConsumer(QueueConfig queueConfig) {
        this.queueConfig = queueConfig;
    }

    @Override
    public TaskExecutionResult execute(Task<VerifiedData> task) {
        try {
            Timestamp beginVerification = new Timestamp(System.currentTimeMillis());
            VerifiedData verifiedData = task.getPayloadOrThrow();
            Long idRequestSubsidyFile = Long.valueOf(verifiedData.getIdentificator());
            Long idRequestSubsidyFileSignature = Long.valueOf(verifiedData.getSignatureIdentificator());
            Long idRegVerificationSignatureFile = Long.valueOf(verifiedData.getGroup());

            TpRequestSubsidyFile dataFile = tpRequestSubsidyFileRepo.findById(idRequestSubsidyFile).orElse(null);
            TpRequestSubsidyFile signatureFile = tpRequestSubsidyFileRepo.findById(idRequestSubsidyFileSignature).orElse(null);
            RegVerificationSignatureFile rvsf = regVerificationSignatureFileRepo.findById(idRegVerificationSignatureFile).orElse(null);
            //RegVerificationSignatureFile rvsf = regVerificationSignatureFileRepo.findByIdCustom(idRegVerificationSignatureFile).orElse(null);

            if (dataFile != null && signatureFile != null && rvsf != null) {
                CMSVerifier cmsVerifier = process(dataFile, signatureFile, rvsf);

                final RegVerificationSignatureFile verificationSignatureFile = saveRegVerificationSignatureFile(cmsVerifier, rvsf, beginVerification);
                verificationLog.info("Задача выполнена " + verificationSignatureFile.toString());
            } else {
                verificationLog.error("Не удалось получить задачу из очереди: " + task.toString());
            }

        } catch (Exception ex) {
            verificationLog.error("Не удалось получить задачу из очереди: " + task.toString());
            verificationLog.error(ex.getMessage(), ex);
        }

        return TaskExecutionResult.finish();
    }

    private CMSVerifier process(TpRequestSubsidyFile dataFile, TpRequestSubsidyFile signatureFile, RegVerificationSignatureFile rvsf){
        final String absolutePath = Paths.get(uploadingAttachmentDir).toFile().getAbsolutePath();
        String dataFileName = absolutePath + File.separator + dataFile.getAttachmentPath();
        String signatureFileName = absolutePath + File.separator + signatureFile.getAttachmentPath();

        File file = new File(dataFileName);
        File signature = new File(signatureFileName);

        VerifiedData verifiedData = new VerifiedData(signature.getAbsolutePath(), file.getAbsolutePath(),
                dataFile.getId(), signatureFile.getId(), rvsf.getId());

        final CMSVerifier cmsVerifier = verifyMessageService.buildCMSVerifier(verifiedData);
        cmsVerifier.verify();
        return cmsVerifier;
    }

    private RegVerificationSignatureFile saveRegVerificationSignatureFile(CMSVerifier cmsVerifier,
                                         RegVerificationSignatureFile rvsf, Timestamp beginVerification){
        //но надо будет искать немного по-другому как минимум с учетом принципала и потом еще последний срез у этого принципала
//        RegVerificationSignatureFile rvsf =
//                regVerificationSignatureFileRepo.findByRequestSubsidy_IdAndRequestSubsidyFile_IdAndRequestSubsidySubsidySignatureFile_Id
//                        (dataFile.getRequestSubsidy().getId(), dataFile.getId(), signatureFile.getId());

        rvsf.setVerifyResult(verifyMessageService.getVerifyResult(cmsVerifier));
        //    -- 1 - проверка прошла успешно
        //    -- 2 - подпись не соответствует файлу
        //    -- 3  в сертификате или цепочке сертификатов есть ошибки
        //    -- 4 в подписи есть ошибки
        if (cmsVerifier.isSuccess()){
            rvsf.setVerifyStatus(1);
        }else if (!cmsVerifier.signatureSuccessVerify()){
            rvsf.setVerifyStatus(2);
        }else if (!cmsVerifier.certificateSuccessVerify()){
            rvsf.setVerifyStatus(3);
        }else{
            rvsf.setVerifyStatus(4);
        }
        rvsf.setTimeBeginVerification(beginVerification);
        rvsf.setTimeEndVerification(new Timestamp(System.currentTimeMillis()));
        return regVerificationSignatureFileRepo.save(rvsf);
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
