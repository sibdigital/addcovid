package ru.sibdigital.addcovid.service.file;

import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.dto.RegOrganizationInspectionFileDto;
import ru.sibdigital.addcovid.model.RegOrganizationInspectionFile;

import java.util.List;

public interface InspectionFileService {
    List<RegOrganizationInspectionFile> getInspectionFilesByInspectionId(Long inspectionId);
    RegOrganizationInspectionFile uploadInspectionFile(MultipartFile file, Long idInspection);
    Boolean saveInspectionFiles(List<RegOrganizationInspectionFileDto> orgInspectionFileDtos, Long idInspection);
}
