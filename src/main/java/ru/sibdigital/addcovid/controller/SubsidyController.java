package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class SubsidyController {
    //для работы с ClsSubsidy, TpRequiredSubsidyFile TpSubsidyFile TpSubsidyOkved  ClsSubsidyRequestStatus
    @PostMapping(value = "/upload_subsidy_files")
    public ResponseEntity<String> addEmployeeFromExcel(@RequestParam(value = "filepond") MultipartFile file, HttpSession session){
        if (!file.isEmpty()) {
            System.out.println(file);
        }
        return ResponseEntity.ok().body("{ \"status\": \"server\", \"sname\": \"" + "check}");
    }
}
