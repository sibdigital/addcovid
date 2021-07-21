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
            long countVerifySignatures = regVerificationSignatureFiles.stream()
                    .filter(rvsf -> rvsf.getVerifyStatus() != 0 && rvsf.getUser() != null)
                    .count();
            long countNumberOfFiles = regVerificationSignatureFiles.stream()
                    .filter(rvsf -> rvsf.getUser() != null)
                    .count();

            result.put("files", regVerificationSignatureFiles);
            result.put("verified", countVerifySignatures);
            result.put("numberOfFiles", countNumberOfFiles);
        }
        return result;
    }
}
