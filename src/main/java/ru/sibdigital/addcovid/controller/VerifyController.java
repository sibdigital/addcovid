package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Manager;
import org.apache.catalina.manager.ManagerServlet;
import org.apache.catalina.session.StandardSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.subs.RegVerificationSignatureFile;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.subs.RegVerificationSignatureFileRepo;
import ru.sibdigital.addcovid.repository.subs.TpRequestSubsidyFileRepo;
import ru.sibdigital.addcovid.service.queue.CustomQueueService;
import ru.sibdigital.addcovid.service.subs.RequestSubsidyService;
import ru.sibdigital.addcovid.utils.DataFormatUtils;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Controller
public class VerifyController {

    private final static Logger verificationLog = LoggerFactory.getLogger("VerificationLogger");

    @Autowired
    private CustomQueueService customQueueService;

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private RegVerificationSignatureFileRepo regVerificationSignatureFileRepo;

    @Autowired
    private TpRequestSubsidyFileRepo tpRequestSubsidyFileRepo;

    @Autowired
    private RequestSubsidyService requestSubsidyService;

    private final RequestSubsidyController requestSubsidyController = new RequestSubsidyController();

    @PostMapping("/verify/subsidy/file")
    @ResponseBody
    public ResponseEntity<String> testVerifyProcess(@RequestBody VerifiedData verifiedData) {
        String status = "Проверка электронной подписи начата";
        Long enqueue = -1L;
        try {
            enqueue = customQueueService.enqueue(verifiedData);
            verificationLog.info("Проверка ИОГВ: " + verifiedData);
        }catch (Exception ex){
            verificationLog.error(ex.getMessage(), ex);
            log.error(ex.getMessage(), ex);
            status = "Ошибка добавления в очередь проверки ЭП";
        }
        return DataFormatUtils.buildResponse(ResponseEntity.ok(),
                Map.of("status", status,"task_id", enqueue));
    }

    @PostMapping("/verify/subsidy/list")
    @ResponseBody
    public ResponseEntity<String> testVerifyProcess(@RequestBody List<VerifiedData> verifiedDataList) {
        String status = "Проверка электронных подписей начата";
        List<Long> tasks =  new ArrayList<>();
        try {
            for (VerifiedData verifiedData : verifiedDataList) {
                System.out.println(verifiedData);
            }
            tasks = customQueueService.enqueueAll(verifiedDataList);
            verificationLog.info("Проверка ИОГВ: " + verifiedDataList);
        } catch (Exception ex) {
            verificationLog.error(ex.getMessage(), ex);
            log.error(ex.getMessage(), ex);
            status = "Ошибка добавления в очередь проверки ЭП";
        }
        return DataFormatUtils.buildResponse(ResponseEntity.ok(),
                Map.of("status", status,"sname", tasks));
    }

    @GetMapping(value = "/verify/subsidy/check_signature_files_verify_progress")
    public @ResponseBody HashMap<String, Object> checkSignatureFilesVerifyProgress(
            @RequestParam("id_request") Long idRequest,
            HttpSession session) {
        HashMap<String, Object> result = new HashMap<>();

        List<RegVerificationSignatureFile> regVerificationSignatureFiles =
                regVerificationSignatureFileRepo.findByIdRequest(idRequest).orElse(null);

        if (regVerificationSignatureFiles == null) {
            result.put("notFound", "Файлы не найдены");
        } else {
            int countVerifySignatures = 0;
            int countNumberOfFiles = 0;
            for (RegVerificationSignatureFile regVerificationSignatureFile : regVerificationSignatureFiles) {
                if (regVerificationSignatureFile.getVerifyStatus() != 0 && regVerificationSignatureFile.getUser() != null) {
                    countVerifySignatures++;
                }
                if (regVerificationSignatureFile.getUser() != null) {
                    countNumberOfFiles++;
                }
            }

            result.put("files", regVerificationSignatureFiles);
            result.put("verified", countVerifySignatures);
            result.put("numberOfFiles", countNumberOfFiles);
        }
        return result;
    }

    @GetMapping(value = "/verify/subsidy/check_request_subsidy_files_signatures")
    public ResponseEntity<String> checkRequestSubsidyFilesSignatures(
            @RequestParam("id_request") Long idRequest,
            @RequestParam("id_organization") Long id,
            HttpSession session) {
        ClsOrganization clsOrganization = null;
        if (id != null) {
            clsOrganization = clsOrganizationRepo.findById(id).orElse(null);
//            clsOrganization = new ClsOrganization();
        }
        if (id == null || clsOrganization == null) {
            return DataFormatUtils.buildInternalServerErrorResponse(
                    Map.of("cause", "Не найден id организации", "status", "error", "sname", ""));
        }
        List<TpRequestSubsidyFile> signatureFiles = tpRequestSubsidyFileRepo.getSignatureFilesByIdRequest(idRequest).orElse(null);
        if (signatureFiles == null) {
            return DataFormatUtils.buildResponse(ResponseEntity.ok(),
                    Map.of("cause", "Не найдены файлы подписи", "status", "error", "sname", ""));
        }
        List<VerifiedData> verifiedDataList = new ArrayList<>();
        for (TpRequestSubsidyFile signatureFile : signatureFiles) {
            TpRequestSubsidyFile docFile = signatureFile.getRequestSubsidyFile();
            final RegVerificationSignatureFile rvsf = constructVerificationSignatureFile(signatureFile, docFile, clsOrganization);
            regVerificationSignatureFileRepo.save(rvsf);

            VerifiedData verifiedData = new VerifiedData(
                    signatureFile.getAttachmentPath(),
                    docFile.getAttachmentPath(),
                    docFile.getId(),
                    signatureFile.getId(),
                    rvsf.getId()
            );
            verifiedDataList.add(verifiedData);
        }
        customQueueService.enqueueAll(verifiedDataList);
        final long waiting = requestSubsidyService.getWaitingStartTaskExecution(new Date());
        String waitingInterface = (waiting <= 60) ? "1 мин." : ((waiting / 60) + "  мин. " + (waiting % 60) + "  сек. ");
        return DataFormatUtils.buildOkResponse(
                Map.of("cause","До начала проверки подписей не менее " + waitingInterface,
                        "status", "ok", "sname", "check"));
    }

    private RegVerificationSignatureFile constructVerificationSignatureFile(TpRequestSubsidyFile signatureFile,
                                                                            TpRequestSubsidyFile docFile, ClsOrganization clsOrganization){
        List<RegVerificationSignatureFile> previsious =
                regVerificationSignatureFileRepo.findByPrevisiousVerification(clsOrganization.getPrincipal(), signatureFile.getRequestSubsidy(), signatureFile, docFile);
        if (previsious.isEmpty()) {
            RegVerificationSignatureFile regVerificationSignatureFile = RegVerificationSignatureFile.builder()
                    .requestSubsidy(docFile.getRequestSubsidy())
                    .requestSubsidyFile(docFile)
                    .requestSubsidySubsidySignatureFile(signatureFile)
                    .isDeleted(false)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .verifyStatus(0)
                    .principal(clsOrganization.getPrincipal())
                    .build();
            previsious.add(regVerificationSignatureFile);
        } else {
            previsious.stream().forEach(p -> {
                p.setTimeCreate(new Timestamp(System.currentTimeMillis()));
                p.setVerifyStatus(0);
                p.setVerifyResult("");
            });
        }
        return previsious.get(0); //подразумевается, что лежит только одна запись о проверке
    }
}
