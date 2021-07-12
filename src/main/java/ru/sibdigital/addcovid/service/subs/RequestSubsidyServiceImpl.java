package ru.sibdigital.addcovid.service.subs;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyPostDto;
import ru.sibdigital.addcovid.model.ActivityStatuses;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.subs.ClsSubsidy;
import ru.sibdigital.addcovid.model.subs.ClsSubsidyRequestStatus;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.subs.ClsSubsidyRepo;
import ru.sibdigital.addcovid.repository.subs.ClsSubsidyRequestStatusRepo;
import ru.sibdigital.addcovid.repository.subs.DocRequestSubsidyRepo;

import java.sql.Timestamp;

@Log4j2
@Service
public class RequestSubsidyServiceImpl implements RequestSubsidyService{
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile
    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    ClsSubsidyRepo clsSubsidyRepo;

    @Autowired
    ClsSubsidyRequestStatusRepo clsSubsidyRequestStatusRepo;

    @Autowired
    DocRequestSubsidyRepo docRequestSubsidyRepo;

    @Override
    public void saveNewDocRequestSubsidy(DocRequestSubsidyPostDto postFormDto) {
        ClsOrganization organization = clsOrganizationRepo.findById(postFormDto.getOrganizationId()).orElse(null);
        ClsSubsidy subsidy = clsSubsidyRepo.findById(postFormDto.getSubsidyId()).orElse(null);
        ClsSubsidyRequestStatus subsidyRequestStatus = clsSubsidyRequestStatusRepo.findByCode(postFormDto.getSubsidyRequestStatusCode());
        DocRequestSubsidy docRequestSubsidy = docRequestSubsidyRepo.findById(postFormDto.getId()).orElse(null);

        if (postFormDto.getId() != null) {
            docRequestSubsidy.setDepartment(subsidy.getDepartment());
            docRequestSubsidy.setSubsidyRequestStatus(subsidyRequestStatus);
            docRequestSubsidy.setTimeUpdate(new Timestamp(System.currentTimeMillis()));
            docRequestSubsidy.setReqBasis(postFormDto.getReqBasis());
            docRequestSubsidy.setSubsidy(subsidy);
            docRequestSubsidy.setStatusActivity(ActivityStatuses.ACTIVE.getValue());
            docRequestSubsidy.setDeleted(false);
        } else  {
            docRequestSubsidy = DocRequestSubsidy.builder()
                    .organization(organization)
                    .department(subsidy.getDepartment())
                    .subsidyRequestStatus(subsidyRequestStatus)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .timeUpdate(new Timestamp(System.currentTimeMillis()))
                    .reqBasis(postFormDto.getReqBasis())
                    .subsidy(subsidy)
                    .statusActivity(ActivityStatuses.ACTIVE.getValue())
                    .isDeleted(false)
                    .build();
        }

        if (postFormDto.getSubsidyRequestStatusCode().equals("SUBMIT")) {
            docRequestSubsidy.setTimeSend(new Timestamp(System.currentTimeMillis()));
        }

        docRequestSubsidyRepo.save(docRequestSubsidy);
    }

    @Override
    public DocRequestSubsidy saveDocRequestSubsidyDraft(DocRequestSubsidyPostDto postFormDto) {
        ClsOrganization organization = clsOrganizationRepo.findById(postFormDto.getOrganizationId()).orElse(null);
        ClsSubsidy subsidy = clsSubsidyRepo.findById(postFormDto.getSubsidyId()).orElse(null);
        ClsSubsidyRequestStatus subsidyRequestStatus = clsSubsidyRequestStatusRepo.findByCode("NEW");
        DocRequestSubsidy docRequestSubsidy = DocRequestSubsidy.builder()
                    .organization(organization)
                    .department(subsidy.getDepartment())
                    .subsidyRequestStatus(subsidyRequestStatus)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .reqBasis(postFormDto.getReqBasis())
                    .subsidy(subsidy)
                    .isDeleted(true)
                    .build();

        docRequestSubsidyRepo.save(docRequestSubsidy);
        return docRequestSubsidy;
    }
}
