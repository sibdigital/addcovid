package ru.sibdigital.addcovid.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.dto.EmployeeDto;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.model.ClsExcel;
import ru.sibdigital.addcovid.model.RequestTypes;
import ru.sibdigital.addcovid.parser.CheckProtocol;
import ru.sibdigital.addcovid.parser.ExcelParser;
import ru.sibdigital.addcovid.repository.ClsExcelRepo;
import ru.sibdigital.addcovid.service.RequestService;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Log4j2
@Controller
public class FileUploadController {

    @Autowired
    ExcelParser excelParser;

    @Autowired
    RequestService requestService;

    @Autowired
    ClsExcelRepo excelRepo;

    @Value("${upload_xls.path:/upload_xls}")
    String uploadingDir;

    @Value("${upload.path:/uploads}")
    String uploadingAttacchmentDir;

    @Autowired
    private ApplicationConstants applicationConstants;

    private Base64.Encoder enc = Base64.getEncoder();

    @RequestMapping("/upload")
    public String forwardUpload(Model model) {
        //this.getClass().getClassLoader().getResource("template.xlsx");
        //model.addAttribute("errorMessage", null);

        model.addAttribute("errorMessage", model.getAttribute("errorMessage")); //hack to inject value inside html
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        model.addAttribute("DOWNLOAD_XLSX_TEMPLATE", applicationConstants.getRefDownloadXlsxTemplate());
        model.addAttribute("XLSX_FILL_INSTRUCTION", applicationConstants.getRefXlsxFillInstruction());
        model.addAttribute("FAQ", applicationConstants.getRefFaq());
        return "upload";
    }

    @RequestMapping("/upload/protocol")
    public String uploadProtocol(ModelMap model) {

        model.addAttribute("errorMessage", model.getAttribute("errorMessage")); //hack to inject value inside html

        CheckProtocol checkProtocol = (CheckProtocol) model.getAttribute("checkProtocol");  //hack to inject value inside html


        if (model.getAttribute("checkProtocol") == null){
            return "redirect:/upload";
        }


        model.addAttribute("checkProtocol", checkProtocol);
        model.addAttribute("postFormDto", checkProtocol.getPostFormDto()); //hack to inject value inside html
        model.addAttribute("application_name", applicationConstants.getApplicationName());

        return "upload_protocol";


    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String  uploadFiles(@RequestParam("excelFile") MultipartFile excelFile,
                                    @RequestParam("pdfFile") MultipartFile pdfFile,
                                    @RequestParam("isActualization") boolean isActualization,
                                    @RequestParam("isAgreed") boolean isAgreed,
                                    @RequestParam("isProtected") boolean isProtected,
                                    @RequestParam("reqBasis") String reqBasis,
                                    RedirectAttributes rm) throws IOException {

        if( !isAgreed ){
            rm.addFlashAttribute("errorMessage", "Необходимо принять соглашение на обработку персональных данных");
            return "redirect:/upload";
        }
        if( !isProtected){
            rm.addFlashAttribute("errorMessage", "Необходимо принять предписание Управления Роспотребнадзора по Республике Бурятия");
            return "redirect:/upload";
        }

        ClsExcel excelRecord = ClsExcel.builder().timeUpload(Timestamp.valueOf(LocalDateTime.now())).build();
        try{
            File savedFile = saveFile(excelFile);
            excelRecord.setName(savedFile.getName());
            if(savedFile == null){
                rm.addFlashAttribute("errorMessage", "Невозможно прочитать файл!");
                return "redirect:/upload";
            }
            CheckProtocol checkProtocol = excelParser.parseFile(savedFile);


            //вынужденная мера из-за конфлитка с реализацией RequestServic
            File f = saveAttachment(pdfFile);
//            StringBuilder stringBuilder = new StringBuilder();
//            byte[] encbytes = enc.encode(pdfFile.getBytes());
//            for (int i = 0; i < encbytes.length; i++)
//            {
//                stringBuilder.append((char)encbytes[i]);
//            }

            PostFormDto postFormDto = checkProtocol.getPostFormDto();

            postFormDto.setAttachmentFilename(pdfFile.getOriginalFilename());
            postFormDto.setAttachment(f.getName());
            postFormDto.setIsActualization(isActualization);
            postFormDto.setIsAgree(isAgreed);
            postFormDto.setIsProtect(isProtected);
            postFormDto.setReqBasis(reqBasis);

            if(checkProtocol.isSuccess()) {
                requestService.addNewRequest(postFormDto, RequestTypes.ORGANIZATION.getValue());
                excelRecord.setStatus(0);
            } else {
                excelRecord.setStatus(1);
                excelRecord.setDescription(checkProtocol.getErrors());
            }

            rm.addFlashAttribute("checkProtocol",checkProtocol);
            rm.addFlashAttribute("postFormDto",checkProtocol.getPostFormDto());
            excelRepo.save(excelRecord);
            return "redirect:/upload/protocol";


        } catch (IOException ex){
            rm.addFlashAttribute("errorMessage", ex.getMessage());
            log.error("uploadFiles()", ex);
            excelRecord.setStatus(1);
            excelRecord.setDescription(ex.getMessage());
            excelRepo.save(excelRecord);

        }  catch (Exception ex) {
            rm.addFlashAttribute("uploadFiles()", ex.getMessage());
            log.error("ERROR", ex);
            if(ex instanceof SQLException){

            } else {
                excelRecord.setStatus(1);
                excelRecord.setDescription(ex.getMessage());
                excelRepo.save(excelRecord);
            }
        }


        return "redirect:/upload";
    }

    private File saveAttachment(MultipartFile pdfFile){
        File file = null;
        String filename = "error while upload";
        try {
            File uploadFolder = new File(uploadingAttacchmentDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            file = new File(String.format("%s/%s_%s",uploadFolder.getAbsolutePath(), String.valueOf(System.currentTimeMillis()),
                    pdfFile.getOriginalFilename().replaceAll("[!@#$&~%*()\\^\\[\\]{}'\"\\:>< ,;/?|`]", "_")));
            pdfFile.transferTo(file);

        } catch (IOException ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error("saveFile(): xls file was not saved cause:", ex);
        } catch (Exception ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error("saveFile(): xls file was not saved cause:", ex);
        }
        return file;
    }

    private File  saveFile(MultipartFile excelFile){
        File f = null;
        try {
            String name = excelFile.getOriginalFilename();

            File uploadFolder = new File(uploadingDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String fname = name.length() > 50 ? (excelFile.getName().substring(0, 50) + ".xls") : name;
            String inputFilename = String.format("%s/%s_%s", uploadFolder.getAbsolutePath(), String.valueOf(System.currentTimeMillis()), fname);

            f = new File(inputFilename);
            excelFile.transferTo(f);

        } catch (IOException ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error("saveFile(): xls file was not saved cause:", ex);
        } catch (Exception ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error("saveFile(): xls file was not saved cause:", ex);
        }
        return f;
    }

    @RequestMapping(value="/download/template", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity downloadFile() {

        URL url = getClass().getClassLoader().getResource("template.xlsx");
        UrlResource resource = new UrlResource(url);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping(value = "/import-excel")
    public ResponseEntity<String> addEmployeeFromExcel(@RequestParam(value = "upload") MultipartFile file, HttpSession session){
        Long id = (Long) session.getAttribute("id_organization");
        File uploadedFile = saveExcelFile(file);
        try {
            List<EmployeeDto> employeesList = excelParser.getEmployeesFromExcel(uploadedFile, id);
            employeesList.forEach(s -> requestService.saveEmployee(s));

        }catch (IOException ex){
            log.error("ERROR", ex);
            return ResponseEntity.ok().body("{ \"status\": \"error\" }");
        }

        return ResponseEntity.ok().body("{ \"status\": \"server\", \"sname\": \"" + String.format("/%s/%s", uploadingDir, file.getName()) + "\" }");
    }

    //TODO дублирует saveAttachment
    private File saveExcelFile(MultipartFile file){
        File uploadDir = new File(uploadingDir);
        File uploadedFile = null;
        try {
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            } else {
                deleteAnyFilesInDir(uploadDir);
            }
            uploadedFile = saveFile(file);
        }
        catch (Exception ex) {
            log.error(String.format("file was not saved cause: %s", ex.getMessage()));
        }
        return uploadedFile;
    }

    //Удаление прочих файлов
    public void deleteAnyFilesInDir(File uploadDir){
        String[] entries = uploadDir.list();
        for(String s: entries){
            File currentFile = new File(uploadDir.getPath(),s);
            currentFile.delete();
        }
    }

}
