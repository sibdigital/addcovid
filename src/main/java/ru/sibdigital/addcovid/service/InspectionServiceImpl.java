package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.dto.RegOrganizationInspectionDto;
import ru.sibdigital.addcovid.model.ClsControlAuthority;
import ru.sibdigital.addcovid.model.ClsInspectionResult;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.RegOrganizationInspection;
import ru.sibdigital.addcovid.repository.ClsControlAuthorityRepo;
import ru.sibdigital.addcovid.repository.ClsInspectionResultRepo;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.RegOrganizationInspectionRepo;

@Service
@Slf4j
public class InspectionServiceImpl implements InspectionService {

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    ClsControlAuthorityRepo clsControlAuthorityRepo;

    @Autowired
    ClsInspectionResultRepo clsInspectionResultRepo;

    @Autowired
    RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @Override
    public RegOrganizationInspection saveInspection(RegOrganizationInspectionDto inspectionDto) {

        ClsOrganization organization = clsOrganizationRepo.findById(inspectionDto.getOrganizationId()).orElse(null);
        ClsControlAuthority authority = clsControlAuthorityRepo.findById(inspectionDto.getControlAuthorityId()).orElse(null);
        ClsInspectionResult result = clsInspectionResultRepo.findById(inspectionDto.getInspectionResultId()).orElse(null);

        RegOrganizationInspection inspection = RegOrganizationInspection.builder()
                    .id(inspectionDto.getId())
                    .organization(organization)
                    .dateOfInspection(inspectionDto.getDateOfInspection())
                    .controlAuthority(authority)
                    .inspectionResult(result)
                    .comment(inspectionDto.getComment())
                    .build();

        regOrganizationInspectionRepo.save(inspection);

        return inspection;
    }
}
