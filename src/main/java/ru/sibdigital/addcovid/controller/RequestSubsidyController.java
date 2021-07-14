package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.sibdigital.addcovid.dto.DocRequestDto;
import ru.sibdigital.addcovid.dto.KeyValue;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyDto;
import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyPostDto;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.model.subs.ClsSubsidy;
import ru.sibdigital.addcovid.model.subs.DocRequestSubsidy;
import ru.sibdigital.addcovid.model.subs.RegVerificationSignatureFile;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;
import ru.sibdigital.addcovid.repository.ClsFileTypeRepo;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.ClsSettingsRepo;
import ru.sibdigital.addcovid.repository.subs.ClsSubsidyRepo;
import ru.sibdigital.addcovid.repository.subs.DocRequestSubsidyRepo;
import ru.sibdigital.addcovid.repository.subs.RegVerificationSignatureFileRepo;
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
import java.util.stream.Collectors;

@Slf4j
@Controller
public class RequestSubsidyController {
    //для работы с DocRequestSubsidy, RegVerificationSignatureFile, TpRequestSubsidyFile
    @Autowired
    RequestSubsidyService requestSubsidyService;

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    ClsSubsidyRepo clsSubsidyRepo;

    @Autowired
    DocRequestSubsidyRepo docRequestSubsidyRepo;

    @Autowired
    ClsSettingsRepo clsSettingsRepo;

    @Autowired
    private ClsFileTypeRepo clsFileTypeRepo;

    @Autowired
    private TpRequestSubsidyFileRepo tpRequestSubsidyFileRepo;

    @Autowired
    private RegVerificationSignatureFileRepo regVerificationSignatureFileRepo;

    @Value("${upload.path:/uploads}")
    String uploadingAttachmentDir;

    @GetMapping("/org_request_subsidies")
    public @ResponseBody
    List<DocRequestSubsidyDto> getRequests(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<DocRequestSubsidy> requestSubsidies = docRequestSubsidyRepo.findAllByOrganizationIdAndStatusActivity(organization.getId(),
                ActivityStatuses.ACTIVE.getValue()).orElse(new ArrayList<>());
        List<DocRequestSubsidyDto> dtos = requestSubsidies.stream()
                .map(docRequestSubsidy -> DocRequestSubsidyDto.builder()
                                            .id(docRequestSubsidy.getId())
                                            .subsidyId((docRequestSubsidy.getSubsidy() != null) ? docRequestSubsidy.getSubsidy().getId() : null)
                                            .subsidyName((docRequestSubsidy.getSubsidy() != null) ? docRequestSubsidy.getSubsidy().getShortName() : "")
                                            .departmentName((docRequestSubsidy.getDepartment() != null) ? docRequestSubsidy.getDepartment().getName() : "")
                                            .subsidyRequestStatusName((docRequestSubsidy.getSubsidyRequestStatus() != null) ? docRequestSubsidy.getSubsidyRequestStatus().getShortName() : "")
                                            .subsidyRequestStatusCode((docRequestSubsidy.getSubsidyRequestStatus() != null) ? docRequestSubsidy.getSubsidyRequestStatus().getCode() : "")
                                            .timeCreate(docRequestSubsidy.getTimeCreate())
                                            .timeUpdate(docRequestSubsidy.getTimeUpdate())
                                            .timeReview(docRequestSubsidy.getTimeReview())
                                            .timeSend(docRequestSubsidy.getTimeSend())
                                            .reqBasis(docRequestSubsidy.getReqBasis())
                                            .resolutionComment(docRequestSubsidy.getResolutionComment())
                                            .subsidyName(docRequestSubsidy.getSubsidy().getShortName())
                                            .districtName((docRequestSubsidy.getDistrict() != null) ? docRequestSubsidy.getDistrict().getName() : "")
//                                            .statusActivityName(docRequestSubsidy.getStatusActivity().toString())
                                            .build())
                .collect(Collectors.toList());

        return dtos;
    }

    @GetMapping("/check_right_to_apply_request_subsidy")
    public @ResponseBody
    Boolean checkRightsToApplyRequestSubsidy(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<ClsSubsidy> subsidies = clsSubsidyRepo.getListSubsidyForOrganization(organization.getId(), organization.getIdTypeOrganization());
        if (subsidies.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @GetMapping("/no_right_to_apply_request_subsidy_message")
    public @ResponseBody
    String noRightsToApplyRequestSubsidyMessage(HttpSession session) {
        ClsSettings settings = clsSettingsRepo.getActualByKey("noRightToApplyRequestSubsidy").orElse(null);
        String message = "";
        if (settings != null) {
            message = settings.getStringValue();
        }

        return message;
    }

    @GetMapping("/available_subsidies")
    public @ResponseBody
    List<KeyValue> getAvailableSubsidies(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<ClsSubsidy> subsidies = clsSubsidyRepo.getAvailableSubsidiesForOrganization(organization.getId(), organization.getIdTypeOrganization());

        List<KeyValue> list = subsidies.stream()
                .map(ctr -> new KeyValue(ctr.getClass().getSimpleName(), ctr.getId(), ctr.getName()))
                .collect(Collectors.toList());
        return list;
    }

    @PostMapping("/save_request_subsidy")
    public @ResponseBody ResponseEntity<String> postNewRequest(@RequestBody DocRequestSubsidyPostDto postFormDto, HttpSession session) {
        try {
            Long id = (Long) session.getAttribute("id_organization");
            if (id == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"cause\": \"Не найден id организации\"," +
                                "\"status\": \"server\"," +
                                "\"sname\": \"" + "" + "\"}");
            }

            postFormDto.setOrganizationId(id);

//            String errors = validateNewRequestSubsidy(postFormDto);
//            if (errors.isEmpty()) {
            requestSubsidyService.saveNewDocRequestSubsidy(postFormDto);
            if (postFormDto.getSubsidyRequestStatusCode().equals("SUBMIT")) {
                return ResponseEntity.ok()
                        .body("{\"cause\": \"Заявка принята. Ожидайте ответ на электронную почту.\"," +
                                "\"status\": \"server\"," +
                                "\"sname\": \"" + "success" + "\"}");
            } else {
                return ResponseEntity.ok()
                        .body("{\"cause\": \"Заявка сохранена.\"," +
                                "\"status\": \"server\"," +
                                "\"sname\": \"" + "success" + "\"}");
            }

//            } else {
//                return errors;
//            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"cause\": \"Невозможно подать заявку\"," +
                            "\"status\": \"server\"," +
                            "\"sname\": \"" + "error" + "\"}");
        }
    }


    @GetMapping(value = "/request_subsidy_files")
    public @ResponseBody List<TpRequestSubsidyFile> uploadRequiredSubsidyFiles(
            @RequestParam("doc_request_subsidy_id") Long request_subsidy_id,
            @RequestParam("id_file_type") Long id_file_type) {
        List<TpRequestSubsidyFile> tpRequestSubsidyFiles = requestSubsidyService.findAllRequestSubsidyFilesByRequestAndFileType(request_subsidy_id, id_file_type);
        return tpRequestSubsidyFiles;
    }

    @PostMapping(value = "/upload_subsidy_files")
    public ResponseEntity<String> uploadRequiredSubsidyFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("id_file_type") Long id_file_type) {

        if (files.length != 2) {
            return ResponseEntity.ok()
                    .body("{\"cause\": \"Ошибка при загрузке\"," +
                            "\"status\": \"server\"," +
                            "\"sname\": \"Ожидалось 2 файла\"}");
        } else {
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
            if (sortedFiles[1] == null) {
                return ResponseEntity.ok()
                        .body("{\"cause\": \"Ошибка при загрузке подписи\"," +
                            "\"status\": \"server\"," +
                            "\"sname\": \"Файл с электронной подписью не найден\"}");
            }
            Arrays.stream(sortedFiles).forEach(file -> {
               saveFile(file, docRequestSubsidy, clsFileType);
            });

            return ResponseEntity.ok().body("{ \"status\": \"server\", \"sname\": \"Документ успешно загружен\" }");
        }
    }

    @PostMapping(value = "/set_subsidy_file_view_name")
    public ResponseEntity<String> setSubsidyFileViewName(
            @RequestParam("id_subsidy_file") Long id,
            @RequestParam("view_name") String viewName) {
        TpRequestSubsidyFile tpRequestSubsidyFile = tpRequestSubsidyFileRepo.findById(id).orElse(null);
        tpRequestSubsidyFile.setViewFileName(viewName);
        tpRequestSubsidyFileRepo.save(tpRequestSubsidyFile);
        return ResponseEntity.ok()
                .body("{\"status\": \"server\"," +
                        "\"sname\": \"" + tpRequestSubsidyFile + "\"}");
    }

    @PostMapping(value = "/del_request_subsidy_file")
    public ResponseEntity<String> delRequestSubsidyFile(
            @RequestParam("id_subsidy_file") Long id) {
        TpRequestSubsidyFile tpRequestSubsidyFile = tpRequestSubsidyFileRepo.findById(id).orElse(null);
        TpRequestSubsidyFile tpRequestSubsidyFileSignature = requestSubsidyService.findSignatureFile(tpRequestSubsidyFile.getId());
        tpRequestSubsidyFileRepo.delete(tpRequestSubsidyFileSignature);
        tpRequestSubsidyFileRepo.delete(tpRequestSubsidyFile);
        return ResponseEntity.ok()
                .body("{\"status\": \"server\"," +
                        "\"sname\": \"" + tpRequestSubsidyFile + "\"}");
    }

    @GetMapping(value = "/check_signature_files_verify_progress")
    public @ResponseBody HashMap<String,Object> checkSignatureFilesVerifyProgress(
            @RequestParam("id_request") Long idRequest, HttpSession session) {
        HashMap<String,Object> result = new HashMap<>();
        Long idOrganization = (Long) session.getAttribute("id_organization");
        if (idOrganization == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(idOrganization).orElse(null);

        List<RegVerificationSignatureFile> regVerificationSignatureFiles =
                regVerificationSignatureFileRepo.findByIdRequestAndIdPrincipal(idRequest,
                        organization.getPrincipal()).orElse(null);

        int countVerifySignatures = 0;
        for(RegVerificationSignatureFile regVerificationSignatureFile : regVerificationSignatureFiles) {
            if(regVerificationSignatureFile.getVerifyStatus() != 0){
                countVerifySignatures++;
            }
        }

        result.put("files", regVerificationSignatureFiles);
        result.put("verified", countVerifySignatures);
        result.put("numberOfFiles", regVerificationSignatureFiles.size());
        return result;
    }

    @GetMapping(value = "/check_request_subsidy_files_signatures")
    public ResponseEntity<String> checkRequestSubsidyFilesSignatures(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"cause\": \"Не найден id организации\"," +
                            "\"status\": \"server\"," +
                            "\"sname\": \"" + "" + "\"}");
        }
        ClsOrganization clsOrganization = clsOrganizationRepo.findById(id).orElse(null);
        List<TpRequestSubsidyFile> signatureFiles = tpRequestSubsidyFileRepo.getSignatureFiles().orElse(null);
        List<VerifiedData> verifiedDataList = new ArrayList<>();
         for(TpRequestSubsidyFile signatureFile : signatureFiles){
             TpRequestSubsidyFile docFile = tpRequestSubsidyFileRepo.getDocFilesBySignature(signatureFile.getRequestSubsidyFile().getId()).orElse(null);
             VerifiedData verifiedData = new VerifiedData(
                     signatureFile.getAttachmentPath(),
                     docFile.getAttachmentPath(),
                     docFile.getId(),
                     signatureFile.getId(),
                     docFile.getRequestSubsidy().getId()
             );
             if(!verifiedData.isEmptyData()){
                 RegVerificationSignatureFile regVerificationSignatureFile = RegVerificationSignatureFile.builder()
                         .requestSubsidy(docFile.getRequestSubsidy())
                         .requestSubsidyFile(docFile)
                         .requestSubsidySubsidySignatureFile(signatureFile)
                         .timeCreate(new Timestamp(System.currentTimeMillis()))
                         .isDeleted(false)
                         .verifyStatus(0)
                         .principal(clsOrganization.getPrincipal())
                         .build();
                 RegVerificationSignatureFile regVerificationSignatureFileExistFile = regVerificationSignatureFileRepo
                         .findByIdFileAndIdSignature(docFile.getId(),signatureFile.getId()).orElse(null);
                 if(regVerificationSignatureFileExistFile == null){
                    regVerificationSignatureFileRepo.save(regVerificationSignatureFile);
                 }
                 verifiedDataList.add(verifiedData);
             }
         }
        return ResponseEntity.ok()
                .body("{\"status\":\"server\"," +
                        "\"sname\":\"check\"}");
    }

    //File writer
    private TpRequestSubsidyFile saveFile(MultipartFile file, DocRequestSubsidy docRequestSubsidy, ClsFileType clsFileType){
        File f = null;
        TpRequestSubsidyFile savedRequestSubsidyFile = null;
        try {
            String name = file.getOriginalFilename();
            String extension = getFileExtension(name);

            TpRequestSubsidyFile parentDocSubsidyFile = null;
            Boolean isSignature = false;
            if (extension.equals(".p7s")) {
                Long idLastRequestSubsidyFile = requestSubsidyService.findLastRequestSubsidyFile();
                parentDocSubsidyFile = tpRequestSubsidyFileRepo.findById(idLastRequestSubsidyFile).orElse(null);
                isSignature = true;
            }

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
                    .requestSubsidyFile(parentDocSubsidyFile)
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
