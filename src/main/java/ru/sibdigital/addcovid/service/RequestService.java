package ru.sibdigital.addcovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.DocRequest;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.DocAddressFactRepo;
import ru.sibdigital.addcovid.repository.DocPersonRepo;
import ru.sibdigital.addcovid.repository.DocRequestRepo;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class RequestService {

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    DocAddressFactRepo docAddressFactRepo;

    @Autowired
    DocPersonRepo docPersonRepo;

    @Autowired
    DocRequestRepo docRequestRepo;



    public void addNewRequst(PostFormDto postForm){

        ClsOrganization organization;

        if(postForm.getOrganizationId() != null){
            organization = clsOrganizationRepo.getOne(postForm.getOrganizationId());
        } else {
            organization = ClsOrganization.builder()
                    .name(postForm.getOrganizationName())
                    .shortName(postForm.getOrganizationShortName())
                    .inn(postForm.getOrganizationInn())
                    .ogrn(postForm.getOrganizationOgrn())
                    .addressJur(postForm.getOrganizationAddressJur())
                    .okvedAdd(postForm.getOrganizationOkvedAdd())
                    .okved(postForm.getOrganizationOkved())
                    .email(postForm.getOrganizationEmail())
                    .phone(postForm.getOrganizationPhone())
                    .statusImport(0)
                    .timeImport(Timestamp.valueOf(LocalDateTime.now()))
                    .build();
            organization = clsOrganizationRepo.save(organization);
        }

        DocRequest docRequest = DocRequest.builder()
                .personOfficeCnt(postForm.getPersonOfficeCnt())
                .personOfficeFactCnt(postForm.getPersonOfficeFactCnt())
                .personRemoteCnt(postForm.getPersonRemoteCnt())
                .personSlrySaveCnt(postForm.getPersonSlrySaveCnt())
                .attachmentPath("")
                .statusReview(0)
                .timeReview(Timestamp.valueOf(LocalDateTime.now()))
                .statusImport(0)
                .timeImport(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        docRequest = docRequestRepo.save(docRequest);











    }
}
