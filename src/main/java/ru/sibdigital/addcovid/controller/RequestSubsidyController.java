package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.dto.DocRequestDto;
import ru.sibdigital.addcovid.dto.KeyValue;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyDto;
import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyPostDto;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.model.subs.ClsSubsidy;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.ClsSettingsRepo;
import ru.sibdigital.addcovid.repository.subs.ClsSubsidyRepo;
import ru.sibdigital.addcovid.repository.subs.DocRequestSubsidyRepo;
import ru.sibdigital.addcovid.service.subs.RequestSubsidyService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class RequestSubsidyController {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile
    @Autowired
    RequestSubsidyService requestSubsidyService;

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    ClsSubsidyRepo clsSubsidyRepo;

    @Autowired
    DocRequestSubsidyRepo docRequestSubsidyRepo;

    @Autowired
    ClsSettingsRepo clsSettingsRepo;

    @GetMapping("/org_request_subsidies")
    public @ResponseBody
    List<DocRequestSubsidyDto> getRequests(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<DocRequestSubsidy> requestSubsidies = docRequestSubsidyRepo.findAllByOrganizationIdAndStatusActivityAndIsDeleted(organization.getId(),
                ActivityStatuses.ACTIVE.getValue(), false).orElse(new ArrayList<>());
        List<DocRequestSubsidyDto> dtos = requestSubsidies.stream()
                .map(docRequestSubsidy -> DocRequestSubsidyDto.builder()
                                            .id(docRequestSubsidy.getId())
                                            .subsidyId((docRequestSubsidy.getSubsidy() != null) ? docRequestSubsidy.getSubsidy().getId() : null)
                                            .subsidyName((docRequestSubsidy.getSubsidy() != null) ? docRequestSubsidy.getSubsidy().getShortName() : "")
                                            .departmentName((docRequestSubsidy.getDepartment() != null) ? docRequestSubsidy.getDepartment().getName() : "")
                                            .subsidyRequestStatusName((docRequestSubsidy.getSubsidyRequestStatus() != null) ? docRequestSubsidy.getSubsidyRequestStatus().getShortName() : "")
                                            .subsidyRequestStatusCode((docRequestSubsidy.getSubsidyRequestStatus() != null) ? docRequestSubsidy.getSubsidyRequestStatus().getCode() : "")
                                            .timeCreate(docRequestSubsidy.getTimeCreate())
                                            .timeUpdate(docRequestSubsidy.getTimeUpdate())
                                            .timeReview(docRequestSubsidy.getTimeReview())
                                            .timeSend(docRequestSubsidy.getTimeSend())
                                            .reqBasis(docRequestSubsidy.getReqBasis())
                                            .resolutionComment(docRequestSubsidy.getResolutionComment())
                                            .subsidyName(docRequestSubsidy.getSubsidy().getShortName())
                                            .districtName((docRequestSubsidy.getDistrict() != null) ? docRequestSubsidy.getDistrict().getName() : "")
                                            .isDeleted(docRequestSubsidy.getDeleted())
//                                            .statusActivityName(docRequestSubsidy.getStatusActivity().toString())
                                            .build())
                .collect(Collectors.toList());

        return dtos;
    }

    @GetMapping("/check_right_to_apply_request_subsidy")
    public @ResponseBody
    Boolean checkRightsToApplyRequestSubsidy(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<ClsSubsidy> subsidies = clsSubsidyRepo.getListSubsidyForOrganization(organization.getId(), organization.getIdTypeOrganization());
        if (subsidies.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @GetMapping("/no_right_to_apply_request_subsidy_message")
    public @ResponseBody
    String noRightsToApplyRequestSubsidyMessage(HttpSession session) {
        ClsSettings settings = clsSettingsRepo.getActualByKey("noRightToApplyRequestSubsidy").orElse(null);
        String message = "";
        if (settings != null) {
            message = settings.getStringValue();
        }

        return message;
    }

    @GetMapping("/available_subsidies")
    public @ResponseBody
    List<KeyValue> getAvailableSubsidies(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<ClsSubsidy> subsidies = clsSubsidyRepo.getAvailableSubsidiesForOrganization(organization.getId(), organization.getIdTypeOrganization());

        List<KeyValue> list = subsidies.stream()
                .map(ctr -> new KeyValue(ctr.getClass().getSimpleName(), ctr.getId(), ctr.getShortName()))
                .collect(Collectors.toList());
        return list;
    }

    @PostMapping("/save_request_subsidy_draft")
    public @ResponseBody DocRequestSubsidy saveRequestSubsidyDraft(@RequestBody DocRequestSubsidyPostDto postFormDto, HttpSession session) {
        try {
            Long id = (Long) session.getAttribute("id_organization");
            if (id == null) {
                return null;
            }

            postFormDto.setOrganizationId(id);

            DocRequestSubsidy draft = requestSubsidyService.saveDocRequestSubsidyDraft(postFormDto);
            return draft;
        } catch (Exception e) {
           return null;
        }
    }

    @PostMapping("/save_request_subsidy")
    public @ResponseBody ResponseEntity<String> postNewRequest(@RequestBody DocRequestSubsidyPostDto postFormDto, HttpSession session) {
        try {
            Long id = (Long) session.getAttribute("id_organization");
            if (id == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"cause\": \"Не найден id организации\"," +
                                "\"status\": \"server\"," +
                                "\"sname\": \"" + "" + "\"}");
            }

            postFormDto.setOrganizationId(id);

//            String errors = validateNewRequestSubsidy(postFormDto);
//            if (errors.isEmpty()) {
            requestSubsidyService.saveNewDocRequestSubsidy(postFormDto);
            if (postFormDto.getSubsidyRequestStatusCode().equals("SUBMIT")) {
                return ResponseEntity.ok()
                        .body("{\"cause\": \"Заявка принята. Ожидайте ответ на электронную почту.\"," +
                                "\"status\": \"server\"," +
                                "\"sname\": \"" + "success" + "\"}");
            } else {
                return ResponseEntity.ok()
                        .body("{\"cause\": \"Заявка сохранена.\"," +
                                "\"status\": \"server\"," +
                                "\"sname\": \"" + "success" + "\"}");
            }

//            } else {
//                return errors;
//            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"cause\": \"Невозможно подать заявку\"," +
                            "\"status\": \"server\"," +
                            "\"sname\": \"" + "error" + "\"}");
        }
    }
}
