package ru.sibdigital.addcovid.service;

import ru.sibdigital.addcovid.dto.OrganizationDto;
import ru.sibdigital.addcovid.model.ClsOrganization;

import java.util.List;

public interface OrganizationService {

    ClsOrganization saveNewClsOrganization(OrganizationDto organization);

    ClsOrganization saveNewClsOrganizationAsActivated(OrganizationDto organization);

    ClsOrganization saveClsOrganization(ClsOrganization organization);

    ClsOrganization findById(Long id);

    ClsOrganization findByInnAndPrincipalIsNotNull(String inn, List<Integer> typeOrganizations);

    List<OrganizationDto> getOrganizationsByEsia();
}
