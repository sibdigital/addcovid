package ru.sibdigital.addcovid.service.file;

import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.model.RegOrganizationInspection;
import ru.sibdigital.addcovid.model.RegOrganizationInspectionFile;

import java.util.List;

public interface InspectionFileService {
    List<RegOrganizationInspectionFile> getInspectionFilesByInspectionId(Long inspectionId);
    RegOrganizationInspectionFile saveInspectionFile(MultipartFile file, Long idInspection);
    RegOrganizationInspectionFile markInspectionFileAsDeletedById(Long inspectionFileId);
}
