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
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.dto.KeyValue;
import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyDto;
import ru.sibdigital.addcovid.dto.subs.DocRequestSubsidyPostDto;
import ru.sibdigital.addcovid.dto.subs.TpRequestSubsidyFileDto;
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
import ru.sibdigital.addcovid.service.EmailService;
import ru.sibdigital.addcovid.service.queue.CustomQueueService;
import ru.sibdigital.addcovid.service.subs.RequestSubsidyService;
import ru.sibdigital.addcovid.utils.DataFormatUtils;
import ru.sibdigital.addcovid.utils.FileUtils;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
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

    @Autowired
    private CustomQueueService customQueueService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationConstants applicationConstants;

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
        List<DocRequestSubsidy> requestSubsidies = docRequestSubsidyRepo.findAllByOrganizationIdAndStatusActivityAndIsDeleted(organization.getId(),
                ActivityStatuses.ACTIVE.getValue(), false).orElse(new ArrayList<>());
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
                        .isDeleted(docRequestSubsidy.getDeleted())
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
        return !subsidies.isEmpty();
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
    List<ClsSubsidy> getAvailableSubsidies(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<ClsSubsidy> subsidies = clsSubsidyRepo.getAvailableSubsidiesForOrganization(organization.getId(), organization.getIdTypeOrganization());

//        List<KeyValue> list = subsidies.stream()
//                .map(ctr -> new KeyValue(ctr.getClass().getSimpleName(), ctr.getId(), ctr.getShortName()))
//                .collect(Collectors.toList());
//        return list;
        return subsidies;
    }

    @PostMapping("/save_request_subsidy_draft")
    public @ResponseBody
    DocRequestSubsidy saveRequestSubsidyDraft(@RequestBody DocRequestSubsidyPostDto postFormDto, HttpSession session) {
        try {
            Long id = (Long) session.getAttribute("id_organization");
            if (id == null) {
                return null;
            }

            postFormDto.setOrganizationId(id);

            DocRequestSubsidy draft = requestSubsidyService.saveDocRequestSubsidyDraft(postFormDto);
            return draft;
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/save_request_subsidy")
    public @ResponseBody
    ResponseEntity<String> postNewRequest(@RequestBody DocRequestSubsidyPostDto postFormDto, HttpSession session) {
        try {
            Long id = (Long) session.getAttribute("id_organization");
            if (id == null) {
                return DataFormatUtils.buildInternalServerErrorResponse(Map.of(
                        "status", "server", "sname", "error", "cause", "Не найден id организации"));
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("{\"cause\": \"Не найден id организации\"," +
//                                "\"status\": \"server\"," +
//                                "\"sname\": \"" + "" + "\"}");
            }

            postFormDto.setOrganizationId(id);
            ClsOrganization clsOrganization = clsOrganizationRepo.findById(id).orElse(null);

//            String errors = validateNewRequestSubsidy(postFormDto);
//            if (errors.isEmpty()) {
            requestSubsidyService.saveNewDocRequestSubsidy(postFormDto);
            if (postFormDto.getSubsidyRequestStatusCode().equals("SUBMIT")) {
                if (clsOrganization != null && clsOrganization.getEmail() != null) {
                    ClsSettings clsSettings = clsSettingsRepo.getActualByKey("submit_request_subsidy_message").orElse(null);
                    if (clsSettings != null && clsSettings.getStringValue() != null && !clsSettings.getStringValue().equals("")) {
                        emailService.sendSimpleMessage(clsOrganization.getEmail(), applicationConstants.getApplicationName(), clsSettings.getStringValue());
                    }
                }
                return DataFormatUtils.buildOkResponse(Map.of(
                        "status", "server", "sname", "success", "cause", "Заявка принята. Ожидайте ответ на электронную почту"));
//                return ResponseEntity.ok()
//                        .body("{\"cause\": \"Заявка принята. Ожидайте ответ на электронную почту.\"," +
//                                "\"status\": \"server\"," +
//                                "\"sname\": \"" + "success" + "\"}");
            } else {
                return DataFormatUtils.buildOkResponse(Map.of(
                        "status", "server", "sname", "success", "cause", "Заявка сохранена"));
//                return ResponseEntity.ok()
//                        .body("{\"cause\": \"Заявка сохранена.\"," +
//                                "\"status\": \"server\"," +
//                                "\"sname\": \"" + "success" + "\"}");
            }

//            } else {
//                return errors;
//            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return DataFormatUtils.buildInternalServerErrorResponse(Map.of(
                    "status", "server", "sname", "error", "cause", "Невозможно подать заявку"));
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("{\"cause\": \"Невозможно подать заявку\"," +
//                            "\"status\": \"server\"," +
//                            "\"sname\": \"" + "error" + "\"}");
        }
    }


    @GetMapping(value = "/request_subsidy_files")
    public @ResponseBody
    List<TpRequestSubsidyFileDto> uploadRequiredSubsidyFiles(
            @RequestParam("doc_request_subsidy_id") Long request_subsidy_id,
            @RequestParam("id_file_type") Long id_file_type) {
        List<TpRequestSubsidyFile> tpRequestSubsidyFiles = requestSubsidyService.findAllRequestSubsidyFilesByRequestAndFileType(request_subsidy_id, id_file_type);
        List<TpRequestSubsidyFileDto> tpRequestSubsidyFileDtos = new ArrayList<>();

        if (tpRequestSubsidyFiles != null) {
            for (TpRequestSubsidyFile docFile : tpRequestSubsidyFiles) {
                TpRequestSubsidyFile signatureFile = tpRequestSubsidyFileRepo.getSignatureFileByDocFileId(docFile.getId()).orElse(null);
                RegVerificationSignatureFile verificationSignatureFile = null;
                if(signatureFile != null) {
                    verificationSignatureFile = regVerificationSignatureFileRepo.findByIdFileAndIdSignature(docFile.getId(),signatureFile.getId()).orElse(null);
                }
                TpRequestSubsidyFileDto tpRequestSubsidyFileDto = TpRequestSubsidyFileDto.builder()
                        .docFile(docFile)
                        .signatureFile(signatureFile)
                        .verificationSignatureFile(verificationSignatureFile)
                        .build();
                tpRequestSubsidyFileDtos.add(tpRequestSubsidyFileDto);
            }
        }

        return tpRequestSubsidyFileDtos;
    }

    @PostMapping(value = "/upload_subsidy_files")
    public ResponseEntity<String> uploadRequiredSubsidyFiles(
            @RequestParam("upload") MultipartFile file,
            @RequestParam(value = "id_file_type", required = false) Long id_file_type,
            @RequestParam(value = "id_request", required = false) Long idRequest,
            @RequestParam(value = "id_request_subsidy_file", required = false) Long idRequestSubsidyFile) {
        TpRequestSubsidyFile tpRequestSubsidyFile = null;
        ClsFileType clsFileType = clsFileTypeRepo.findById(id_file_type).orElse(null);
        DocRequestSubsidy docRequestSubsidy = docRequestSubsidyRepo.findById(idRequest).orElse(null);

        if (idRequestSubsidyFile != null) {
            tpRequestSubsidyFile = tpRequestSubsidyFileRepo.findById(idRequestSubsidyFile).orElse(null);
        }

        saveFile(file, docRequestSubsidy, clsFileType, tpRequestSubsidyFile);

        return DataFormatUtils.buildOkResponse(Map.of("status", "server", "sname", "Документ успешно загружен"));
        //return ResponseEntity.ok().body("{ \"status\": \"server\", \"sname\": \"Документ успешно загружен\" }");
    }

    @PostMapping(value = "/set_subsidy_file_view_name")
    public ResponseEntity<String> setSubsidyFileViewName(
            @RequestParam("id_subsidy_file") Long id,
            @RequestParam("view_name") String viewName) {
        TpRequestSubsidyFile tpRequestSubsidyFile = tpRequestSubsidyFileRepo.findById(id).orElse(null);
        tpRequestSubsidyFile.setViewFileName(viewName);
        tpRequestSubsidyFileRepo.save(tpRequestSubsidyFile);
        return DataFormatUtils.buildOkResponse(Map.of("status", "server", "sname", tpRequestSubsidyFile));
//        return ResponseEntity.ok()
//                .body("{\"status\": \"server\"," +
//                        "\"sname\": \"" + tpRequestSubsidyFile + "\"}");
    }

    @PostMapping(value = "/del_request_subsidy_file")
    public ResponseEntity<String> delRequestSubsidyFile(
            @RequestParam("id_subsidy_file") Long id) {
        TpRequestSubsidyFile tpRequestSubsidyFile = tpRequestSubsidyFileRepo.findById(id).orElse(null);
        TpRequestSubsidyFile tpRequestSubsidyFileSignature = requestSubsidyService.findSignatureFile(tpRequestSubsidyFile.getId());
//        tpRequestSubsidyFileRepo.delete(tpRequestSubsidyFileSignature);
//        tpRequestSubsidyFileRepo.delete(tpRequestSubsidyFile);
        if (tpRequestSubsidyFile != null) {
            tpRequestSubsidyFile.setDeleted(true);
            tpRequestSubsidyFileRepo.save(tpRequestSubsidyFile);
        }
        if (tpRequestSubsidyFileSignature != null) {
            tpRequestSubsidyFileSignature.setDeleted(true);
            tpRequestSubsidyFileRepo.save(tpRequestSubsidyFileSignature);
        }
        if (tpRequestSubsidyFile != null && tpRequestSubsidyFileSignature != null) {
            RegVerificationSignatureFile rvsf = regVerificationSignatureFileRepo.findByIdFileAndIdSignature(tpRequestSubsidyFile.getId(), tpRequestSubsidyFileSignature.getId()).orElse(null);
            if (rvsf != null) {
                rvsf.setIsDeleted(true);
                regVerificationSignatureFileRepo.save(rvsf);
            }
        }

        return DataFormatUtils.buildOkResponse(Map.of("status", "server", "sname", tpRequestSubsidyFile));
//                ResponseEntity.ok()
//                .body("{\"status\": \"server\"," +
//                        "\"sname\": \"" + tpRequestSubsidyFile + "\"}");
    }

    @GetMapping(value = "/check_signature_files_verify_progress")
    public @ResponseBody HashMap<String, Object> checkSignatureFilesVerifyProgress(
            @RequestParam("id_request") Long idRequest, HttpSession session) {
        HashMap<String, Object> result = new HashMap<>();
        Long idOrganization = (Long) session.getAttribute("id_organization");
        if (idOrganization == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(idOrganization).orElse(null);

        List<RegVerificationSignatureFile> regVerificationSignatureFiles =
                regVerificationSignatureFileRepo.findByIdRequestAndIdPrincipal(idRequest,
                        organization.getPrincipal()).orElse(null);

        if (regVerificationSignatureFiles == null) {
            result.put("notFound", "Файлы не найдены");
        } else {
            int countVerifySignatures = 0;
            for (RegVerificationSignatureFile regVerificationSignatureFile : regVerificationSignatureFiles) {
                if (regVerificationSignatureFile.getVerifyStatus() != 0) {
                    countVerifySignatures++;
                }
            }

            result.put("files", regVerificationSignatureFiles);
            result.put("verified", countVerifySignatures);
            result.put("numberOfFiles", regVerificationSignatureFiles.size());
        }
        return result;
    }

    @GetMapping(value = "/check_request_subsidy_files_signatures")
    public ResponseEntity<String> checkRequestSubsidyFilesSignatures(
            @RequestParam("id_request") Long idRequest,
            HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        ClsOrganization clsOrganization = null;
        if (id != null) {
            clsOrganization = clsOrganizationRepo.findById(id).orElse(null);
        }
        if (id == null || clsOrganization == null) {
            return DataFormatUtils.buildInternalServerErrorResponse(
                    Map.of("cause", "Не найден id организации", "status", "error", "sname", ""));
        }
        List<TpRequestSubsidyFile> signatureFiles = tpRequestSubsidyFileRepo.getSignatureFilesByIdRequest(idRequest).orElse(null);
        if (signatureFiles == null) {
            return DataFormatUtils.buildResponse(ResponseEntity.ok(),
                    Map.of("cause", "Не найдены файлы подписи", "status", "error", "sname", ""));
        }
        List<VerifiedData> verifiedDataList = new ArrayList<>();
        for (TpRequestSubsidyFile signatureFile : signatureFiles) {
            TpRequestSubsidyFile docFile = tpRequestSubsidyFileRepo.getDocFilesBySignature(signatureFile.getRequestSubsidyFile().getId()).orElse(null);

            final RegVerificationSignatureFile rvsf = constructVerificationSignatureFile(signatureFile, docFile, clsOrganization);
            regVerificationSignatureFileRepo.save(rvsf);

            VerifiedData verifiedData = new VerifiedData(
                    signatureFile.getAttachmentPath(),
                    docFile.getAttachmentPath(),
                    docFile.getId(),
                    signatureFile.getId(),
                    rvsf.getId()
            );
            verifiedDataList.add(verifiedData);
//            //if(!verifiedData.isEmptyData()){
//            RegVerificationSignatureFile regVerificationSignatureFile = RegVerificationSignatureFile.builder()
//                    .requestSubsidy(docFile.getRequestSubsidy())
//                    .requestSubsidyFile(docFile)
//                    .requestSubsidySubsidySignatureFile(signatureFile)
//                    .isDeleted(false)
//                    .timeCreate(new Timestamp(System.currentTimeMillis()))
//                    .verifyStatus(0)
//                    .principal(clsOrganization.getPrincipal())
//                    .build();
//                 RegVerificationSignatureFile regVerificationSignatureFileExistFile = regVerificationSignatureFileRepo
//                         .findByIdFileAndIdSignature(docFile.getId(),signatureFile.getId()).orElse(null);
//                 if(regVerificationSignatureFileExistFile == null){
//                    regVerificationSignatureFileRepo.save(regVerificationSignatureFile);
//                 }
//            verifiedDataList.add(verifiedData);
//            //}
        }
        customQueueService.enqueueAll(verifiedDataList);
        return DataFormatUtils.buildOkResponse(
                Map.of("cause","Файлы подписей добавлены в очередь","status", "server", "sname", "check"));
    }

    private RegVerificationSignatureFile constructVerificationSignatureFile(TpRequestSubsidyFile signatureFile,
            TpRequestSubsidyFile docFile, ClsOrganization clsOrganization){
        List<RegVerificationSignatureFile> previsious =
                regVerificationSignatureFileRepo.findByPrevisiousVerification(clsOrganization.getPrincipal(), signatureFile.getRequestSubsidy(), signatureFile, docFile);
        if(previsious.isEmpty()){
            RegVerificationSignatureFile regVerificationSignatureFile = RegVerificationSignatureFile.builder()
                    .requestSubsidy(docFile.getRequestSubsidy())
                    .requestSubsidyFile(docFile)
                    .requestSubsidySubsidySignatureFile(signatureFile)
                    .isDeleted(false)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .verifyStatus(0)
                    .principal(clsOrganization.getPrincipal())
                    .build();
            previsious.add(regVerificationSignatureFile);
        }else{
            previsious.stream().forEach(p -> {
                p.setTimeCreate(new Timestamp(System.currentTimeMillis()));
                p.setVerifyStatus(0);
                p.setVerifyResult("");
            });
        }
        return previsious.get(0); //подразумевается, что лежит только одна запись о проверке
    }

    //File writer
    private TpRequestSubsidyFile saveFile(MultipartFile file, DocRequestSubsidy docRequestSubsidy, ClsFileType clsFileType, TpRequestSubsidyFile tpRequestSubsidyDocFile) {
        File f = null;
        TpRequestSubsidyFile savedRequestSubsidyFile = null;
        try {
            String name = file.getOriginalFilename();
            String extension = FileUtils.getFileExtension(name);

            Boolean isSignature = false;

            String directory = getSavedDirectoryPath(docRequestSubsidy);
            File innIdFolder = getSavingDirectory(directory);

            String prefix = String.valueOf(System.currentTimeMillis());
            if (docRequestSubsidy.getOrganization() != null) {
                prefix = docRequestSubsidy.getOrganization().getId().toString();
            }
            final String filename = prefix + "_" + UUID.randomUUID() + extension;
            String absoluteFilename = innIdFolder.getAbsolutePath() + File.separator + filename;
            String relFilename = directory + File.separator + filename;

            f = new File(absoluteFilename);
            file.transferTo(f);

            final long size = Files.size(f.toPath());
            final String fileHash = FileUtils.getFileHash(f);
            if (tpRequestSubsidyDocFile != null) {
                isSignature = true;
                TpRequestSubsidyFile tpRequestSubsidySignatureFileExists = tpRequestSubsidyFileRepo.getSignatureFileByDocFileId(tpRequestSubsidyDocFile.getId()).orElse(null);
                if (tpRequestSubsidySignatureFileExists != null) {
                    RegVerificationSignatureFile regVerificationSignatureFile = regVerificationSignatureFileRepo.findByIdFileAndIdSignature(tpRequestSubsidyDocFile.getId(), tpRequestSubsidySignatureFileExists.getId()).orElse(null);
                    if (regVerificationSignatureFile != null) {
                        regVerificationSignatureFileRepo.delete(regVerificationSignatureFile);
                    }
                    tpRequestSubsidyFileRepo.delete(tpRequestSubsidySignatureFileExists);
                }
            }
            //view file name ???????????????????????????????????
            TpRequestSubsidyFile tpRequestSubsidyFile = TpRequestSubsidyFile.builder()
                    .fileSize(size)
                    .requestSubsidy(docRequestSubsidy)
                    .department(docRequestSubsidy.getDepartment())
                    .organization(docRequestSubsidy.getOrganization())
                    .fileType(clsFileType)
                    .attachmentPath(relFilename)
                    .isDeleted(false)
                    .fileName(f.getName())
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .originalFileName(name)
                    .fileExtension(extension)
                    .hash(fileHash)
                    .timeCreate(new Timestamp(System.currentTimeMillis()))
                    .isSignature(isSignature)
                    .requestSubsidyFile(tpRequestSubsidyDocFile)
                    .build();

            savedRequestSubsidyFile = tpRequestSubsidyFileRepo.save(tpRequestSubsidyFile);
        } catch (Exception ex) {
            log.error("saveFile(): file was not saved cause:", ex);
        }
        return savedRequestSubsidyFile;
    }



    private String getSavedDirectoryPath(DocRequestSubsidy docRequestSubsidy) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(docRequestSubsidy.getTimeCreate().getTime());
        String dir = "subsidy_files" + File.separator +
                cal.get(Calendar.YEAR) + File.separator +
                cal.get(Calendar.MONTH) + File.separator +
                docRequestSubsidy.getOrganization().getInn() + "_" +
                docRequestSubsidy.getId();
        return dir;
    }

    private File getSavingDirectory(String directory) {
        String filepath = uploadingAttachmentDir + File.separator + directory;
        File innIdFolder = new File(filepath);

        if (!innIdFolder.exists()) {
            innIdFolder.mkdirs();
        }
        return innIdFolder;
    }

    @GetMapping("request_subsidy_files/{id_request_subsidy}")
    public @ResponseBody List<TpRequestSubsidyFile> getTpRequestSubsidyFiles(@PathVariable("id_request_subsidy") Long id_request_subsidy, HttpSession session) {
        return tpRequestSubsidyFileRepo.getTpRequestSubsidyFilesByDocRequestId(id_request_subsidy);
    }

    @GetMapping("request_subsidy_files_verification/{id_request_subsidy}")
    public @ResponseBody List<Map<String, String>> getVerificationTpRequestSubsidyFiles(@PathVariable("id_request_subsidy") Long id_request_subsidy, HttpSession session) {

        List<Map<String, String>> list = tpRequestSubsidyFileRepo.getSignatureVerificationTpRequestSubsidyFile(id_request_subsidy);

        return list;
    }

    @GetMapping("check_all_files_are_verified")
    public @ResponseBody Boolean checkAllFilesAreVerified(@RequestParam("id_request_subsidy") Long id_request_subsidy, HttpSession session) {

        List<Map<String, String>> list = tpRequestSubsidyFileRepo.getNotVerifiedTpRequestSubsidyFile(id_request_subsidy);
        if (list.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

}
