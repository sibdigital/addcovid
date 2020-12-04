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
import ru.sibdigital.addcovid.service.OrganizationFileService;
import ru.sibdigital.addcovid.service.RequestService;

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

    @Autowired
    private OrganizationFileService organizationFileService;

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

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
            DocRequest docRequest = idDocRequest == null ?
                    null : docRequestRepo.findById(idDocRequest).orElse(null);

            if (oorg.isPresent()) {

                RegOrganizationFile regOrganizationFile = construct (part, oorg.get(), docRequest);

                if (regOrganizationFile != null){
                    if (regOrganizationFile.getId() == 0) {
                        regOrganizationFile = organizationFileRepo.save(regOrganizationFile);
                        responseEntity = ResponseEntity.ok().body(regOrganizationFile);
                    }else{
                        responseEntity = ResponseEntity.ok()
                                .body("{\"cause\": \"Вы уже загружали этот файл\"" +
                                        "\"file\": \"" + regOrganizationFile.getOriginalFileName() + "\"}");
                    }
                }else{
                    responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"cause\": \"Ошибка сохранения\"}");
                }
            } else {
                responseEntity = ResponseEntity.badRequest().body("{\"cause\": \"Отсутствует организация\"}");
            }
        }

        return responseEntity;//ResponseEntity.ok().body(requestService.uploadFile(file));
    }

    private RegOrganizationFile construct (MultipartFile part, ClsOrganization organization, DocRequest docRequest){
        RegOrganizationFile rof = null;
        try {

            final String absolutePath = Paths.get(uploadingDir).toFile().getAbsolutePath();
            final String filename = organization.getId().toString() + "_" + UUID.randomUUID();
            final String originalFilename = part.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            File file = new File(String.format("%s/%s.%s", absolutePath, filename, extension));
            part.transferTo(file);

            final String fileHash = getFileHash(file);
            final long size = Files.size(file.toPath());

            final List<RegOrganizationFile> files= organizationFileRepo.findRegOrganizationFileByOrganizationAndHash(organization, fileHash);

            if (!files.isEmpty()){
                rof = files.get(0);
            }else{
                rof = RegOrganizationFile.builder()
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
            }

        } catch (IOException ex){
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        } catch (Exception ex) {
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        }
        return rof;
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
    public @ResponseBody List<RegOrganizationFile> getRegOrgFileName(HttpSession session) {
        Long idOrganization = (Long) session.getAttribute("id_organization");
        ClsOrganization clsOrganization = clsOrganizationRepo.findById(idOrganization).orElse(null);
        return organizationFileRepo.findRegOrganizationFileByOrganization(clsOrganization);
    }

    @PostMapping("/delete_file")
    public @ResponseBody int deleteFile(@RequestBody int id){
        System.out.println(id);
        try{
            organizationFileService.updateFileStatusById(id);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return -1;
        }
        return id;
    }
}
