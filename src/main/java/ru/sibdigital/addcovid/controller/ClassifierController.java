package ru.sibdigital.addcovid.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.dto.EgripResponse;
import ru.sibdigital.addcovid.dto.EgrulResponse;
import ru.sibdigital.addcovid.dto.egrip.EGRIP;
import ru.sibdigital.addcovid.dto.egrul.EGRUL;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrip;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrul;
import ru.sibdigital.addcovid.service.crassifier.EgrulService;

@Log4j2
@RestController
public class ClassifierController {

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private EgrulService egrulService;

    //@CrossOrigin
    @GetMapping("/egrul")
    public EgrulResponse getEgrul(@RequestParam(name = "inn") String inn) {
        EgrulResponse response = new EgrulResponse();
        RegEgrul egrul = egrulService.getEgrul(inn);
        if (egrul != null) {
            try {
                response.build(mapper.readValue(egrul.getData(), EGRUL.СвЮЛ.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            response.empty("По введенному ИНН не найдено юр. лицо");
        }
        return response;
    }

    //@CrossOrigin
    @GetMapping("/egrip")
    public EgripResponse getEgrip(@RequestParam(name = "inn") String inn) {
        EgripResponse response = new EgripResponse();
        RegEgrip egrip = egrulService.getEgrip(inn);
        if (egrip != null) {
            try {
                response.build(mapper.readValue(egrip.getData(), EGRIP.СвИП.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            response.setPossiblySelfEmployed(true);
            response.empty("Если вы являетесь самозанятым, заполните информацию о себе");
        }
        return response;
    }
}
