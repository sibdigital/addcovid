package ru.sibdigital.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.dto.PostFormDto;

import java.util.List;
import java.util.Map;

@Controller
@Log4j2
public class MainController {

    @GetMapping
    public String greeting(Map<String, Object> model) {
        return "index";
    }

    @PostMapping("/")
    public @ResponseBody
    String psotForm(@RequestBody PostFormDto postFormDto) {


        return "success";
    }
}
