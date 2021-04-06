package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.addcovid.dto.KeyValue;
import ru.sibdigital.addcovid.dto.RegOrganizationInspectionDto;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.RegOrganizationInspection;
import ru.sibdigital.addcovid.repository.ClsControlAuthorityRepo;
import ru.sibdigital.addcovid.repository.ClsInspectionResultRepo;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.RegOrganizationInspectionRepo;
import ru.sibdigital.addcovid.service.InspectionService;

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
    public @ResponseBody String saveInspection(@RequestBody RegOrganizationInspectionDto inspectionDto, HttpSession session) {
        RegOrganizationInspection inspection = null;

        Long idOrganization = (Long) session.getAttribute("id_organization");
        inspectionDto.setOrganizationId(idOrganization);

        try {
            inspectionService.saveInspection(inspectionDto);
            return "Сохранено";

        } catch (Exception e) {
            return "Не удалось сохранить";
        }

    }
}
