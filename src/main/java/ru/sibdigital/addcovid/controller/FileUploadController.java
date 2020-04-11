package ru.sibdigital.addcovid.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.parser.CheckProtocol;
import ru.sibdigital.addcovid.parser.ExcelParser;
import ru.sibdigital.addcovid.service.RequestService;

import java.io.IOException;
import java.util.Base64;

@Log4j
@Controller
public class FileUploadController {

    @Autowired
    ExcelParser excelParser;

    @Autowired
    RequestService requestService;

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
            model.addAttribute("errorMessage", "Нужно принять соглашение на обработку персональных данных");
            return "upload";
        }
        if( !isProtected){
            model.addAttribute("errorMessage", "Нужно принять соглашение об обязательном выполнение требований по защите от COVID-19");
            return "upload";
        }


        try{
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

//            if(checkProtocol.isSuccess()) {
//                requestService.addNewRequest(postFormDto);
//            }

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

}
