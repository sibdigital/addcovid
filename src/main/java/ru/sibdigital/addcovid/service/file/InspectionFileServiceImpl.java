package ru.sibdigital.addcovid.service.file;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.repository.RegOrganizationInspectionFileRepo;
import ru.sibdigital.addcovid.repository.RegOrganizationInspectionRepo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
public class InspectionFileServiceImpl extends FileServiceImpl implements InspectionFileService{

    @Autowired
    RegOrganizationInspectionRepo regOrganizationInspectionRepo;

    @Autowired
    RegOrganizationInspectionFileRepo regOrganizationInspectionFileRepo;

    @Value("${upload.path:/uploads}")
    String uploadingDir;

    @Override
    public List<RegOrganizationInspectionFile> getInspectionFilesByInspectionId(Long inspectionId) {
        RegOrganizationInspection inspection = regOrganizationInspectionRepo.findById(inspectionId).orElse(null);
        List<RegOrganizationInspectionFile> list = regOrganizationInspectionFileRepo.findRegOrganizationInspectionFilesByOrganizationInspectionAndIsDeleted(inspection, false).orElse(null);
        return list;
    }

    @Override
    public RegOrganizationInspectionFile saveInspectionFile(MultipartFile file, Long idInspection) {
        RegOrganizationInspection inspection = regOrganizationInspectionRepo.findById(idInspection).orElse(null);

        RegOrganizationInspectionFile inspectionFile = construct(file, inspection);
        if (inspectionFile != null) {
            regOrganizationInspectionFileRepo.save(inspectionFile);
        }

        return inspectionFile;
    }


    private RegOrganizationInspectionFile construct(MultipartFile multipartFile, RegOrganizationInspection inspection) {
        RegOrganizationInspectionFile rnf = null;
        try {
            final String absolutePath = Paths.get(uploadingDir).toFile().getAbsolutePath();

            File directory = new File(absolutePath);
            if (!directory.exists()) {
                directory.mkdir();
            }

            final String filename = inspection.getId().toString() + "n_" + UUID.randomUUID();
            final String originalFilename = multipartFile.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            File file = new File(String.format("%s/%s%s", absolutePath, filename, extension));
            multipartFile.transferTo(file);

            final String fileHash = getFileHash(file);
            final long size = Files.size(file.toPath());

            final List<RegOrganizationInspectionFile> files = new ArrayList<>();

            if (!files.isEmpty()) {
                rnf = files.get(0);
            } else {
                rnf = RegOrganizationInspectionFile.builder()
                        .organizationInspection(inspection)
                        .attachmentPath(String.format("%s/%s", uploadingDir, filename))
                        .fileName(filename)
                        .originalFileName(originalFilename)
                        .isDeleted(false)
                        .fileExtension(extension)
                        .fileSize(size)
                        .hash(fileHash)
                        .timeCreate(new Timestamp(System.currentTimeMillis()))
                        .build();
            }
        } catch (IOException ex) {
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        } catch (Exception ex) {
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        }
        return rnf;
    }

    @Override
    public RegOrganizationInspectionFile markInspectionFileAsDeletedById(Long inspectionFileId) {
        RegOrganizationInspectionFile inspectionFile = regOrganizationInspectionFileRepo.findById(inspectionFileId).orElse(null);
        if (inspectionFile != null) {
            inspectionFile.setDeleted(true);
            regOrganizationInspectionFileRepo.save(inspectionFile);
        }
        return inspectionFile;
    }
}
