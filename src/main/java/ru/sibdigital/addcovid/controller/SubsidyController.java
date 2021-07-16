package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.addcovid.model.subs.TpRequiredSubsidyFile;
import ru.sibdigital.addcovid.repository.subs.DocRequestSubsidyRepo;
import ru.sibdigital.addcovid.service.subs.RequestSubsidyService;

import java.util.*;

@Slf4j
@Controller
public class SubsidyController {
    //для работы с ClsSubsidy, TpRequiredSubsidyFile TpSubsidyFile TpSubsidyOkved  ClsSubsidyRequestStatus

    @Autowired
    private RequestSubsidyService requestSubsidyService;

    @Autowired
    private DocRequestSubsidyRepo docRequestSubsidyRepo;

    @GetMapping(value = "/required_subsidy_files")
    public @ResponseBody List<TpRequiredSubsidyFile> getListTpRequiredSubsidyFiles(
            @RequestParam("idRequest") Long idRequest
    ) {
        DocRequestSubsidy docRequestSubsidy = docRequestSubsidyRepo.findById(idRequest).orElse(null);
        List<TpRequiredSubsidyFile> tpRequiredSubsidyFiles = new ArrayList<>();
        if (docRequestSubsidy != null && docRequestSubsidy.getSubsidy() != null && docRequestSubsidy.getSubsidy().getId() != null) {
             tpRequiredSubsidyFiles = requestSubsidyService.findAllRequiredSubsidyFiles(docRequestSubsidy.getSubsidy().getId());
        }
        return tpRequiredSubsidyFiles;
    }

}
