package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.sibdigital.addcovid.service.queue.CustomQueueService;
import ru.sibdigital.addcovid.utils.DataFormatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class VerifyController {

    private final static Logger verificationLog = LoggerFactory.getLogger("VerificationLogger");

    @Autowired
    private CustomQueueService customQueueService;

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
            tasks = customQueueService.enqueueAll(verifiedDataList);
            verificationLog.info("Проверка ИОГВ: " + verifiedDataList);
        }catch (Exception ex){
            verificationLog.error(ex.getMessage(), ex);
            log.error(ex.getMessage(), ex);
            status = "Ошибка добавления в очередь проверки ЭП";
        }
        return DataFormatUtils.buildResponse(ResponseEntity.ok(),
                Map.of("status", status,"sname", tasks));
    }
}
