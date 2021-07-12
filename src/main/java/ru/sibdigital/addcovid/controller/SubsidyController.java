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
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.addcovid.model.subs.TpRequiredSubsidyFile;
import ru.sibdigital.addcovid.repository.ClsFileTypeRepo;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.subs.DocRequestSubsidyRepo;
import ru.sibdigital.addcovid.repository.subs.TpRequestSubsidyFileRepo;
import ru.sibdigital.addcovid.service.subs.RequestSubsidyService;

import javax.servlet.http.HttpSession;
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
        List<TpRequiredSubsidyFile> tpRequiredSubsidyFiles = requestSubsidyService.findAllRequiredSubsidyFiles(1L);
        return tpRequiredSubsidyFiles;
    }

    @PostMapping(value = "/upload_subsidy_files")
    public ResponseEntity<String> addEmployeeFromExcel(
            @RequestParam MultipartFile[] files,
            @RequestParam("id_file_type") Long id_file_type,
            HttpSession session){
        Long id_organization = (Long) session.getAttribute("id_organization");
        ClsOrganization clsOrganization = clsOrganizationRepo.findById(id_organization).orElse(null);
        ClsFileType clsFileType = clsFileTypeRepo.findById(id_file_type).orElse(null);
        Long doc_request_subsidy_id = 1L;
        DocRequestSubsidy docRequestSubsidy = docRequestSubsidyRepo.findById(doc_request_subsidy_id).orElse(null);
        Arrays.stream(files).forEach(file -> {
            saveFile(file, clsOrganization, docRequestSubsidy, clsFileType);
        });
        return ResponseEntity.ok().body("{ \"status\": \"server\", \"sname\": \"check\" }");
    }

    private File saveFile(MultipartFile file, ClsOrganization clsOrganization, DocRequestSubsidy docRequestSubsidy, ClsFileType clsFileType){
        File f = null;
        try {
            String name = file.getOriginalFilename();
            String extension = getFileExtension(name);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(docRequestSubsidy.getTimeCreate().getTime());

            File innIdFolder = new File(uploadingAttachmentDir + "/subsidy_files/" +
                    cal.get(Calendar.YEAR) + "/" +
                    cal.get(Calendar.MONTH) + "/" +
                    clsOrganization.getInn() + "_" +
                    docRequestSubsidy.getId());

            if (!innIdFolder.exists()) {
                innIdFolder.mkdirs();
            }

            String inputFilename = String.format("%s/%s_%s", innIdFolder.getAbsolutePath(), String.valueOf(System.currentTimeMillis()), name);

            f = new File(inputFilename);
            file.transferTo(f);
            final int size = (int) Files.size(f.toPath());
            final String fileHash = getFileHash(f);
            TpRequestSubsidyFile tpRequestSubsidyFile =
                    TpRequestSubsidyFile.builder()
                            .fileSize(size)
                            .requestSubsidy(docRequestSubsidy)
                            .department(docRequestSubsidy.getDepartment())
                            .organization(clsOrganization)
                            .fileType(clsFileType)
                            .attachmentPath(String.format("%s/%s", uploadingAttachmentDir + "/subsidy_files/" + cal.get(Calendar.YEAR) + "/" + cal.get(Calendar.MONTH) + "/" + clsOrganization.getInn() + "_" + docRequestSubsidy.getId(), f.getName()))
                            .isDeleted(false)
                            .fileName(f.getName())
                            .originalFileName(name)
                            .fileExtension(extension)
                            .hash(fileHash)
                            .timeCreate(new Timestamp(System.currentTimeMillis()))
                            .isSignature(extension.equals(".p7s"))
                            .build();

            tpRequestSubsidyFileRepo.save(tpRequestSubsidyFile);

        } catch (IOException ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error("saveFile(): xls file was not saved cause:", ex);
        } catch (Exception ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error("saveFile(): xls file was not saved cause:", ex);
        }
        return f;
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
}
