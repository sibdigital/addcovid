package ru.sibdigital.addcovid.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.parser.CheckProtocol;
import ru.sibdigital.addcovid.parser.ExcelParser;
import ru.sibdigital.addcovid.service.RequestService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

@Log4j
@Controller
public class FileUploadController {

    @Autowired
    ExcelParser excelParser;

    @Autowired
    RequestService requestService;

    @Value("${upload_xls.path:/upload_xls}")
    String uploadingDir;

    private Base64.Encoder enc = Base64.getEncoder();

    @RequestMapping("/upload")
    public String forwardUpload(Model model) {
        //this.getClass().getClassLoader().getResource("template.xlsx");
        model.addAttribute("errorMessage", null);
        return "upload";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFiles(@RequestParam("excelFile") MultipartFile excelFile,
                              @RequestParam("pdfFile") MultipartFile pdfFile,
                              @RequestParam("isAgreed") boolean isAgreed,
                              @RequestParam("isProtected") boolean isProtected,
                              Model model) throws IOException {

        if( !isAgreed ){
            model.addAttribute("errorMessage", "Необходимо принять соглашение на обработку персональных данных");
            return "upload";
        }
        if( !isProtected){
            model.addAttribute("errorMessage", "Необходимо принять предписание Управления Роспотребнадзора по Республике Бурятия");
            return "upload";
        }


        try{
            saveFile(excelFile);
            CheckProtocol checkProtocol = excelParser.parseFile(excelFile);

            StringBuilder stringBuilder = new StringBuilder();
            byte[] encbytes = enc.encode(pdfFile.getBytes());
            for (int i = 0; i < encbytes.length; i++)
            {
                stringBuilder.append((char)encbytes[i]);
            }

            PostFormDto postFormDto = checkProtocol.getPostFormDto();

            postFormDto.setAttachmentFilename(pdfFile.getOriginalFilename());
            postFormDto.setAttachment(stringBuilder.toString());
            postFormDto.setIsAgree(isAgreed);
            postFormDto.setIsProtect(isProtected);

            if(checkProtocol.isSuccess()) {
                requestService.addNewRequest(postFormDto);
            }

            model.addAttribute("checkProtocol",checkProtocol);
            model.addAttribute("postFormDto",checkProtocol.getPostFormDto());
            return "upload_protocol";




        } catch (IOException ex){
            model.addAttribute("errorMessage", ex.getMessage());
            log.error(ex.getMessage());
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            log.error(ex.getMessage());
        }



//        List<UploadProtocol> protocols = new ArrayList<>(uploadedFiles.length);
//        for(MultipartFile f : uploadedFiles) {
//            protocols.add(uploadAuditor.auditFile(f));
//        }
//        model.addAttribute("protocols", protocols);
        return "upload";
    }

    private void saveFile(MultipartFile pdfFile){
        try {
            String name = pdfFile.getOriginalFilename();

            File uploadFolder = new File(uploadingDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            String fname = name.length() > 50 ? (pdfFile.getName().substring(0, 50) + ".xls") : name;
            String inputFilename = String.format("%s/%s_%s", uploadFolder.getAbsolutePath(), String.valueOf(System.currentTimeMillis()), fname);

            File f = new File(inputFilename);
            pdfFile.transferTo(f);

        } catch (IOException ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error(String.format("xls file was not saved cause: %s", ex.getMessage()));
        } catch (Exception ex) {
            //importStatus = ImportStatuses.FILE_ERROR.getValue();
            log.error(String.format("xls file was not saved cause: %s", ex.getMessage()));
        }
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

}
