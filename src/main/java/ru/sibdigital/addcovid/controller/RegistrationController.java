package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.dto.OrganizationDto;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.service.EmailService;
import ru.sibdigital.addcovid.service.RequestService;

@Slf4j
@Controller
public class RegistrationController {

    @Value("${egrul.address}")
    private String egrulAddress;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    private EmailService emailService;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        if (egrulAddress != null && !egrulAddress.isBlank()) {
            model.addAttribute("egrul_address", egrulAddress);
        }
        return "registration";
    }

    @PostMapping("/registration")
    public @ResponseBody String postRegistration(@RequestBody OrganizationDto organizationDto) {
        try {
            ClsOrganization clsOrganization = requestService.findOrganizationByInn(organizationDto.getOrganizationInn());
            if (clsOrganization != null) {
                return "Данный ИНН уже зарегистрирован в системе";
            }
            clsOrganization = requestService.saveClsOrganization(organizationDto);
            return "Ок";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось зарегистрировать";
        }
    }

    @GetMapping("/recovery")
    public String recovery(Model model) {
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "recovery";
    }

    @PostMapping("/recovery")
    public @ResponseBody String recovery(@RequestParam(name = "inn") String inn) {
        ClsOrganization clsOrganization = requestService.findOrganizationByInn(inn);
        if (clsOrganization != null) {
            String newPassword = requestService.changeOrganizationPassword(inn);
            emailService.sendSimpleMessage(clsOrganization.getEmail(), applicationConstants.getApplicationName(), "Ваш новый пароль от личного кабинета: " + newPassword);
            return "Ок";
        }
        return "ИНН не найден";
    }
}
