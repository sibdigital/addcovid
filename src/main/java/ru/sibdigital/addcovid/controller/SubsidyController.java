package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.model.ClsFileType;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.addcovid.model.subs.TpRequiredSubsidyFile;
import ru.sibdigital.addcovid.repository.ClsFileTypeRepo;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.subs.DocRequestSubsidyRepo;
import ru.sibdigital.addcovid.repository.subs.TpRequestSubsidyFileRepo;
import ru.sibdigital.addcovid.service.subs.RequestSubsidyService;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Controller
public class SubsidyController {
    //для работы с ClsSubsidy, TpRequiredSubsidyFile TpSubsidyFile TpSubsidyOkved  ClsSubsidyRequestStatus

    @Autowired
    private RequestSubsidyService requestSubsidyService;

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private DocRequestSubsidyRepo docRequestSubsidyRepo;

    @Autowired
    private ClsFileTypeRepo clsFileTypeRepo;

    @Autowired
    private TpRequestSubsidyFileRepo tpRequestSubsidyFileRepo;

    @Value("${upload.path:/uploads}")
    String uploadingAttachmentDir;

    @GetMapping(value = "/required_subsidy_files")
    public @ResponseBody List<TpRequiredSubsidyFile> getListTpRequiredSubsidyFiles() {
        DocRequestSubsidy docRequestSubsidy = docRequestSubsidyRepo.findById(1L).orElse(null);
        List<TpRequiredSubsidyFile> tpRequiredSubsidyFiles = requestSubsidyService.findAllRequiredSubsidyFiles(docRequestSubsidy.getSubsidy().getId());
        return tpRequiredSubsidyFiles;
    }

    @PostMapping(value = "/upload_subsidy_files")
    public ResponseEntity<String> uploadRequiredSubsidyFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("id_file_type") Long id_file_type) {
        ClsFileType clsFileType = clsFileTypeRepo.findById(id_file_type).orElse(null);
        DocRequestSubsidy docRequestSubsidy = docRequestSubsidyRepo.findById(1L).orElse(null);

        MultipartFile[] sortedFiles = new MultipartFile[2];
        for (MultipartFile multipartFile : files) {
            if (!getFileExtension(multipartFile.getOriginalFilename()).equals(".p7s")) {
                sortedFiles[0] = multipartFile;
            } else {
                sortedFiles[1] = multipartFile;
            }
        }
        Arrays.stream(sortedFiles).forEach(file -> {
           saveFile(file, docRequestSubsidy, clsFileType);
        });

        return ResponseEntity.ok().body("{ \"status\": \"server\", \"sname\": \"check\" }");
    }

    //File writer
    private TpRequestSubsidyFile saveFile(MultipartFile file, DocRequestSubsidy docRequestSubsidy, ClsFileType clsFileType){
        File f = null;
        TpRequestSubsidyFile savedRequestSubsidyFile = null;
        try {
            String name = file.getOriginalFilename();
            String extension = getFileExtension(name);
            Boolean isSignature = extension.equals(".p7s");
            Long idLastRequestSubsidyFile = tpRequestSubsidyFileRepo.findLastSubsidyFile();
            TpRequestSubsidyFile parentDocSubsidyFile = tpRequestSubsidyFileRepo.findById(idLastRequestSubsidyFile).orElse(null);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(docRequestSubsidy.getTimeCreate().getTime());

            String filepath = uploadingAttachmentDir + "/subsidy_files/" +
                    cal.get(Calendar.YEAR) + "/" +
                    cal.get(Calendar.MONTH) + "/" +
                    docRequestSubsidy.getOrganization().getInn() + "_" +
                    docRequestSubsidy.getId();

            File innIdFolder = new File(filepath);

            if (!innIdFolder.exists()) {
                innIdFolder.mkdirs();
            }

            String inputFilename = String.format("%s/%s_%s", innIdFolder.getAbsolutePath(), String.valueOf(System.currentTimeMillis()), name);

            f = new File(inputFilename);
            file.transferTo(f);

            final int size = (int) Files.size(f.toPath());
            final String fileHash = getFileHash(f);

            TpRequestSubsidyFile tpRequestSubsidyFile = TpRequestSubsidyFile.builder()
                    .fileSize(size)
                    .requestSubsidy(docRequestSubsidy)
                    .department(docRequestSubsidy.getDepartment())
                    .organization(docRequestSubsidy.getOrganization())
                    .fileType(clsFileType)
                    .attachmentPath(String.format("%s/%s", filepath, f.getName()))
                    .isDeleted(false)
                    .fileName(f.getName())
                    .originalFileName(name)
                    .fileExtension(extension)
                    .hash(fileHash)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .isSignature(isSignature)
                    .requestSubsidyFile(isSignature ? parentDocSubsidyFile : null)
                    .build();

            savedRequestSubsidyFile = tpRequestSubsidyFileRepo.save(tpRequestSubsidyFile);
        } catch (Exception ex) {
            log.error("saveFile(): file was not saved cause:", ex);
        }
        return savedRequestSubsidyFile;
    }

    //Files hash
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

    //Files extension
    private String getFileExtension(String name) {
         int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }
}
