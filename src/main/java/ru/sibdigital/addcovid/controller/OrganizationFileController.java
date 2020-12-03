package ru.sibdigital.addcovid.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.DocRequest;
import ru.sibdigital.addcovid.model.RegOrganizationFile;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.DocRequestRepo;
import ru.sibdigital.addcovid.repository.RegOrganizationFileRepo;

import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Controller
public class OrganizationFileController {
    @Autowired
    RegOrganizationFileRepo organizationFileRepo;

    @Autowired
    ClsOrganizationRepo organizationRepo;

    @Autowired
    DocRequestRepo docRequestRepo;

    @Value("${upload.path:/uploads}")
    String uploadingDir;

    @PostMapping(value = "/upload_files", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> uploadFile(@RequestParam(value = "upload") MultipartFile part, HttpSession session,
                                             @RequestParam(required = false) Long idDocRequest){

        Long idOrganization = (Long) session.getAttribute("id_organization");
        ResponseEntity<Object> responseEntity;
        if (Files.notExists(Paths.get(uploadingDir))) {
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"cause\": \"Ошибка сохранения\"}");
        }else {

            final Optional<ClsOrganization> oorg = organizationRepo.findById(idOrganization);

            DocRequest docRequest;
            if (idDocRequest == null) {
                docRequest = null;
            }
            else {
                docRequest = docRequestRepo.findById(idDocRequest).orElse(null);
            }

            if (oorg.isPresent()) {
                final RegOrganizationFile regOrganizationFile = load(part, oorg.get(), docRequest);
                responseEntity = ResponseEntity.ok().body(regOrganizationFile);
            } else {
                responseEntity = ResponseEntity.badRequest().body("{\"cause\": \"Отсутствует организация\"}");
            }
        }

        return responseEntity;//ResponseEntity.ok().body(requestService.uploadFile(file));
    }

    private RegOrganizationFile load (MultipartFile part, ClsOrganization organization, DocRequest docRequest){
        RegOrganizationFile regOrganizationFile = null;
        try {

            final String absolutePath = Paths.get(uploadingDir).toFile().getAbsolutePath();
            final String filename = organization.getId().toString() + "_" + UUID.randomUUID();
            final String originalFilename = part.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            File file = new File(String.format("%s/%s.%s", absolutePath, filename, extension));
            part.transferTo(file);

            final String fileHash = getFileHash(file);
            final long size = Files.size(file.toPath());

            RegOrganizationFile rof = RegOrganizationFile.builder()
                    .clsOrganizationByIdOrganization(organization)
                    .attachmentPath(String.format("%s/%s", uploadingDir, filename))
                    .fileName(filename)
                    .originalFileName(originalFilename)
                    .isDeleted(false)
                    .fileExtension(extension)
                    .fileSize(size)
                    .hash(fileHash)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .build();
            if (docRequest != null){
                rof.setDocRequestByIdRequest(docRequest);
            }

            regOrganizationFile= organizationFileRepo.save(rof);

        } catch (IOException ex){
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        } catch (Exception ex) {
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        }
        return regOrganizationFile;
    }

    private String getFileHash(File file){
        String result = "NOT";
        try {
            final byte[] bytes = Files.readAllBytes(file.toPath());
            byte[] hash = MessageDigest.getInstance("MD5").digest(bytes);
            result = DatatypeConverter.printHexBinary(hash);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex.getMessage());
        }
        return result;
    }

    private String getFileExtension(String name) {
         int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    @GetMapping("/org_files")
    public @ResponseBody List<RegOrganizationFile> getRegOrgFileName() {
        return organizationFileRepo.findAll();
    }
}
