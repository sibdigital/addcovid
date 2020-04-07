package ru.sibdigital.addcovid.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.addcovid.dto.ListItemDto;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.repository.ClsDepartmentRepo;
import ru.sibdigital.addcovid.service.RequestService;

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
            String hash = requestService.addNewRequest(postFormDto).getOrgHashCode();

//            return hash;
            return "Заявка принята. Ожидайте ответ на электронную почту.";
        } catch(Exception e){
            return "Невозможно сохранить заявку";
        }
    }
}
