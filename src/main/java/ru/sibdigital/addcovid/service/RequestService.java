package ru.sibdigital.addcovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.DocAddressFact;
import ru.sibdigital.addcovid.model.DocPerson;
import ru.sibdigital.addcovid.model.DocRequest;
import ru.sibdigital.addcovid.repository.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    ClsDepartmentRepo departmentRepo;



    public String addNewRequst(PostFormDto postForm){

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
                    .hashCode(postForm.sha256())
                    .statusImport(0)
                    .timeImport(Timestamp.valueOf(LocalDateTime.now()))
                    .build();
            organization = clsOrganizationRepo.save(organization);
        }





        Set<DocPerson> personSet = postForm.getPersons()
                .stream()
                .map(personDto -> personDto.convertToPersonEntity())
                .collect(Collectors.toSet());

        Set<DocAddressFact>  docAddressFactSet = postForm.getAddressFact()
                .stream()
                .map(personDto -> personDto.convertToDocAddressFact())
                .collect(Collectors.toSet());

        DocRequest docRequest = DocRequest.builder()
                .organization(organization)
                .department(departmentRepo.getOne(postForm.getDepartmentId()))
                .personOfficeCnt(postForm.getPersonOfficeCnt())
                .personOfficeFactCnt(postForm.getPersonOfficeFactCnt())
                .personRemoteCnt(postForm.getPersonRemoteCnt())
                .personSlrySaveCnt(postForm.getPersonSlrySaveCnt())
                .attachmentPath("")
                .statusReview(0)
                .docPersonSet(personSet)
                .docAddressFact(docAddressFactSet)
                .timeReview(Timestamp.valueOf(LocalDateTime.now()))
                .statusImport(0)
                .timeImport(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        docRequest = docRequestRepo.save(docRequest);

        return organization.getHashCode();








    }
}
