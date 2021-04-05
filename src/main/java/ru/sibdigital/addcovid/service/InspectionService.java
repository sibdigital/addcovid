package ru.sibdigital.addcovid.service;

import ru.sibdigital.addcovid.dto.RegOrganizationInspectionDto;
import ru.sibdigital.addcovid.model.RegOrganizationInspection;

public interface InspectionService {
    RegOrganizationInspection saveInspection(RegOrganizationInspectionDto inspectionDto);
}
