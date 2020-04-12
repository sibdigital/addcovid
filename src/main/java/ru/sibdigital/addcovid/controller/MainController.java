package ru.sibdigital.addcovid.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.dto.DachaDto;
import ru.sibdigital.addcovid.dto.ListItemDto;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.model.DocDacha;
import ru.sibdigital.addcovid.model.DocRequest;
import ru.sibdigital.addcovid.repository.ClsDepartmentRepo;
import ru.sibdigital.addcovid.service.DachaService;
import ru.sibdigital.addcovid.service.RequestService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Controller
public class MainController {

    @Autowired
    private ClsDepartmentRepo clsDepartmentRepo;

    @Autowired
    private RequestService requestService;

    @Autowired
    private DachaService dachaService;

    @GetMapping
    public String greeting(Map<String, Object> model) throws JsonProcessingException {

        List<ListItemDto> listDepartment =  clsDepartmentRepo.findAll()
                .stream()
                .map(clsDepartment -> new ListItemDto(Long.valueOf(clsDepartment.getId()), clsDepartment.getName()))
                .collect(Collectors.toList());

        model.put("listDepartment", listDepartment);
        
        return "index";
    }

    @PostMapping("/")
    public @ResponseBody
    String postForm(@RequestBody PostFormDto postFormDto) {

        try {
            //валидация
            String errors = validate(postFormDto);
            if(errors.isEmpty()){
                DocRequest docRequest = requestService.addNewRequest(postFormDto);

//            return hash;
                return "Заявка принята. Ожидайте ответ на электронную почту.";
            }
            else {
                return "[" + errors + "]";
            }

        } catch(Exception e){
            return "Невозможно сохранить заявку";
        }
    }

    @PostMapping(value = "/uploadpart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam(value = "upload") MultipartFile file){
        return ResponseEntity.ok().body(requestService.uploadFile(file));
    }

    @GetMapping(value = "/download/{id}")
    public void downloadFile(HttpServletResponse response, @PathVariable("id") DocRequest docRequest) throws Exception {
        requestService.downloadFile(response, docRequest);
    }

    private String addError(String field, String msg){
        return "{'field': '" + field + "', 'msg': '" + msg + "'}";
    }

    private String validate(PostFormDto postFormDto){
        String errors = "";
/*
        if(postFormDto.getPersonOfficeCnt() == null || postFormDto.getPersonOfficeCnt() < 0){
            errors = errors + addError("personOfficeCnt", "д.б.>0");
        }
        if(postFormDto.getPersonRemoteCnt() == null || postFormDto.getPersonRemoteCnt() < 0){
            errors = errors + addError("personRemoteCnt", "д.б.<>0");
        }
        if(postFormDto.getPersonSlrySaveCnt() == null || postFormDto.getPersonSlrySaveCnt() < 0){
            errors = errors + addError("personSlrySaveCnt", "д.б.<>0");
        }
*/
        //if(postFormDto.getOrganizationInn().length() < )
        return errors;
    }

    @GetMapping("/dacha")
    public String dacha(Map<String, Object> model) throws JsonProcessingException {
        return "dacha";
    }

    @PostMapping("/dacha")
    public @ResponseBody
    String dachaPostForm(@RequestBody DachaDto dachaDto) {
        try {
            DocDacha docDacha = dachaService.addNewRequest(dachaDto);
            return "Ваша заявка внесена в базу данных дачников";
        } catch(Exception e){
            return "Невозможно сохранить заявку";
        }
    }
}
