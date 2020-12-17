package ru.sibdigital.addcovid.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.dto.OrganizationDto;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.service.EmailService;
import ru.sibdigital.addcovid.service.RequestService;
import ru.sibdigital.addcovid.service.SettingService;

@Slf4j
@Controller
public class RegistrationController {

//    @Value("${egrul.address}")
//    private String egrulAddress;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SettingService settingService;

    private static ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("application_name", applicationConstants.getApplicationName());
//        if (egrulAddress != null && !egrulAddress.isBlank()) {
//            model.addAttribute("egrul_address", egrulAddress);
//        }
        return "registration";
    }

    @GetMapping("/checkInn")
    public @ResponseBody String checkInn(@RequestParam(value = "inn") String inn) {
        ClsOrganization clsOrganization = requestService.findOrganizationByInn(inn);
        if (clsOrganization != null) {
            return "Данный ИНН уже зарегистрирован в системе";
        }
        return "ИНН не зарегистрирован";
    }

    @PostMapping("/registration")
    public @ResponseBody String postRegistration(@RequestBody OrganizationDto organizationDto) {
        try {
            ClsOrganization clsOrganization = requestService.findOrganizationByInn(organizationDto.getOrganizationInn());
            if (clsOrganization != null) {
                return "Данный ИНН уже зарегистрирован в системе";
            }
            clsOrganization = requestService.saveClsOrganization(organizationDto);
            // отправим письмо со ссылкой на активацию
            String activateUrl = settingService.findActualByKey("activationUrl", "");
            activateUrl += "/activate?inn=" + clsOrganization.getInn() + "&code=" + clsOrganization.getHashCode();
            String text = "Здравствуйте! <br/>" +
                    "Для активации учетной записи " + clsOrganization.getName()+ " в личном кабинете на портале Работающая Бурятия перейдите по ссылке: <a href=\"" + activateUrl + "\">Активация учетной записи</a><br/>" +
                    "Если вы не регистрировали личный кабинет на портале Работающая Бурятия, проигнорируйте это сообщение";
            boolean emailSent = emailService.sendSimpleMessageNoAsync(clsOrganization.getEmail(),
                    "Регистрация на портале " + applicationConstants.getApplicationName(), text);
            if (!emailSent) {
                return "Не удалось отправить письмо";
            }
            return "Ок";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось зарегистрировать";
        }
    }

    @GetMapping("/activate")
    public String activate(@RequestParam(value = "inn") String inn, @RequestParam(value = "code") String code, Model model) {
        String message = requestService.activateOrganization(inn, code);
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        model.addAttribute("message", message);
        model.addAttribute("inn", inn);
        return "activate";
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
            boolean emailSent = emailService.sendSimpleMessageNoAsync(clsOrganization.getEmail(), applicationConstants.getApplicationName(), "Ваш новый пароль от личного кабинета: " + newPassword);
            if (!emailSent) {
                return "Не удалось отправить письмо";
            }
            return "Ок";
        }
        return "ИНН не найден";
    }
}
