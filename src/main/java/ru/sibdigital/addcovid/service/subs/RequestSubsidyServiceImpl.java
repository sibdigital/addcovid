package ru.sibdigital.addcovid.service.subs;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyPostDto;
import ru.sibdigital.addcovid.model.ActivityStatuses;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.subs.*;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.subs.*;

import java.sql.Timestamp;
import java.util.List;

@Log4j2
@Service
public class RequestSubsidyServiceImpl implements RequestSubsidyService {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile

    @Autowired
    private TpRequiredSubsidyFileRepo tpRequiredSubsidyFileRepo;

    @Autowired
    private TpRequestSubsidyFileRepo tpRequestSubsidyFileRepo;

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    ClsSubsidyRepo clsSubsidyRepo;

    @Autowired
    ClsSubsidyRequestStatusRepo clsSubsidyRequestStatusRepo;

    @Autowired
    DocRequestSubsidyRepo docRequestSubsidyRepo;

    public List<TpRequiredSubsidyFile> findAllRequiredSubsidyFiles(Long id) {
        return tpRequiredSubsidyFileRepo.findAllBySubsidyIdAndIsDeletedFalse(id).orElse(null);
    }

    public Long findLastRequestSubsidyFile() {
        return tpRequestSubsidyFileRepo.findLastSubsidyFile().orElse(null);
    }

    public List<TpRequestSubsidyFile> findAllRequestSubsidyFilesByRequestAndFileType(Long request_subsidy_id, Long file_type_id) {
        return tpRequestSubsidyFileRepo.findAllRequestSubsidyFilesByRequestAndFileType(request_subsidy_id, file_type_id).orElse(null);
    }

    public TpRequestSubsidyFile findSignatureFile(Long id) {
        return tpRequestSubsidyFileRepo.findSignatureFile(id).orElse(null);
    }

    @Override
    public void saveNewDocRequestSubsidy(DocRequestSubsidyPostDto postFormDto) {
        ClsOrganization organization = clsOrganizationRepo.findById(postFormDto.getOrganizationId()).orElse(null);
        ClsSubsidy subsidy = clsSubsidyRepo.findById(postFormDto.getSubsidyId()).orElse(null);
        ClsSubsidyRequestStatus subsidyRequestStatus = clsSubsidyRequestStatusRepo.findByCode(postFormDto.getSubsidyRequestStatusCode());
        DocRequestSubsidy docRequestSubsidy = null;
        if (postFormDto.getId() != null) {
            docRequestSubsidy = docRequestSubsidyRepo.findById(postFormDto.getId()).orElse(null);
            docRequestSubsidy.setSubsidy(subsidy);
            docRequestSubsidy.setDepartment(subsidy.getDepartment());
            docRequestSubsidy.setSubsidyRequestStatus(subsidyRequestStatus);
            docRequestSubsidy.setReqBasis(postFormDto.getReqBasis());
            docRequestSubsidy.setTimeUpdate(new Timestamp(System.currentTimeMillis()));
        } else {
            docRequestSubsidy = DocRequestSubsidy.builder()
                    .organization(organization)
                    .department(subsidy.getDepartment())
                    .subsidyRequestStatus(subsidyRequestStatus)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .reqBasis(postFormDto.getReqBasis())
                    .subsidy(subsidy)
                    .statusActivity(ActivityStatuses.ACTIVE.getValue())
                    .build();
        }

        if (postFormDto.getSubsidyRequestStatusCode().equals("SUBMIT")) {
            docRequestSubsidy.setTimeSend(new Timestamp(System.currentTimeMillis()));
        }

        docRequestSubsidyRepo.save(docRequestSubsidy);
    }
}