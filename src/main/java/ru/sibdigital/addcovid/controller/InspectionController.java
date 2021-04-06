package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.dto.KeyValue;
import ru.sibdigital.addcovid.dto.RegOrganizationInspectionDto;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.repository.*;
import ru.sibdigital.addcovid.service.InspectionService;
import ru.sibdigital.addcovid.service.file.InspectionFileService;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class InspectionController {

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @Autowired
    private ClsControlAuthorityRepo clsControlAuthorityRepo;

    @Autowired
    private ClsInspectionResultRepo clsInspectionResultRepo;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private InspectionFileService inspectionFileService;

    @GetMapping("/org_inspections")
    public @ResponseBody
    List<RegOrganizationInspection> getInspections(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        if (organization == null) {
            return null;
        }
        List<RegOrganizationInspection> inspections = regOrganizationInspectionRepo.findRegOrganizationInspectionsByOrganization(organization).orElse(null);

        return inspections;
    }

    @GetMapping("/control_authorities_list_short")
    public @ResponseBody List<KeyValue> getControlAuthoritiesForRichselect() {
        List<KeyValue> list = clsControlAuthorityRepo.findAll().stream()
                .map(ctr -> new KeyValue(ctr.getClass().getSimpleName(), ctr.getId(), ctr.getName()))
                .collect(Collectors.toList());

        return list;
    }

    @GetMapping("/inspection_results_list_short")
    public @ResponseBody List<KeyValue> getInspectionResultsForRichselect() {
        List<KeyValue> list = clsInspectionResultRepo.findAll().stream()
                .map(ctr -> new KeyValue(ctr.getClass().getSimpleName(), ctr.getId(), ctr.getName()))
                .collect(Collectors.toList());

        return list;
    }

    @PostMapping("/save_inspection")
    public @ResponseBody RegOrganizationInspection saveInspection(@RequestBody RegOrganizationInspectionDto inspectionDto, HttpSession session) {
        RegOrganizationInspection inspection = null;

        Long idOrganization = (Long) session.getAttribute("id_organization");
        inspectionDto.setOrganizationId(idOrganization);

        try {
            inspection = inspectionService.saveInspection(inspectionDto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return inspection;
    }

    @GetMapping("/inspection_files/{id_inspection}")
    public @ResponseBody List<RegOrganizationInspectionFile> getRegOrgInspectionFiles(@PathVariable("id_inspection") Long inspectionId) {
        if (inspectionId != -1) {
            return inspectionFileService.getInspectionFilesByInspectionId(inspectionId);
        } else
            return null;
    }

    @PostMapping(value = "/upload_inspection_file", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> uploadInspectionFile(@RequestParam(value = "upload") MultipartFile part,
                                             @RequestParam(required = true) Long idInspection){

        RegOrganizationInspectionFile inspectionFile = inspectionFileService.saveInspectionFile(part, idInspection);
        if (inspectionFile != null) {
            return ResponseEntity.ok()
                    .body("{\"cause\": \"Файл успешно загружен\"," +
                            "\"status\": \"server\"," +
                            "\"sname\": \"" + inspectionFile.getOriginalFileName() + "\"}");

        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"status\": \"server\"," +
                        "\"cause\":\"Ошибка сохранения\"}" +
                        "\"sname\": \"" + inspectionFile.getOriginalFileName() + "\"}");
    }

    @PostMapping("/delete_inspection_file")
    public @ResponseBody
    RegOrganizationInspectionFile deleteInspectionFile(@RequestBody Long id){
        try{
            return inspectionFileService.markInspectionFileAsDeletedById(id);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
