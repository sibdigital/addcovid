package ru.sibdigital.addcovid.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.dto.EgripResponse;
import ru.sibdigital.addcovid.dto.EgrulResponse;
import ru.sibdigital.addcovid.dto.OrganizationDto;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.OrganizationTypes;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrip;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrul;
import ru.sibdigital.addcovid.service.EmailService;
import ru.sibdigital.addcovid.service.RequestService;
import ru.sibdigital.addcovid.service.SettingService;
import ru.sibdigital.addcovid.service.crassifier.EgrulService;

import java.util.List;

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

    @Autowired
    private EgrulService egrulService;

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
                    "Для активации учетной записи " + clsOrganization.getName()+ " в личном кабинете на портале " +
                    applicationConstants.getApplicationName() + " перейдите по ссылке: <a href=\"" + activateUrl + "\">Активация учетной записи</a><br/>" +
                    "Если вы не регистрировали личный кабинет на портале " +
                    applicationConstants.getApplicationName() + ", проигнорируйте это сообщение";
//            boolean emailSent = emailService.sendSimpleMessageNoAsync(clsOrganization.getEmail(),
//                    "Регистрация на портале " + applicationConstants.getApplicationName(), text);
//            if (!emailSent) {
//                return "Не удалось отправить письмо";
//            }
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
    public @ResponseBody String recovery(@RequestBody OrganizationDto dto) {
        // TODO переделать, тк по инн могут быть несколько организаций
        ClsOrganization clsOrganization = requestService.findOrganizationByInn(dto.getOrganizationInn());
        if (clsOrganization != null) {
            String inn = dto.getOrganizationInn();
            if (inn == null || inn.isBlank()) {
                return "ИНН не указан";
            }
            String email = dto.getOrganizationEmail();
            if (email == null || email.isBlank()) {
                return "Адрес электронной почты не указан";
            }

            boolean emailLinked;
            try {
                emailLinked = isEmailLinked(clsOrganization, email);
            } catch (Exception e) {
                e.getMessage();
                return "Не удалось проверить адрес электронной почты";
            }

            if (emailLinked) {
                String newPassword = requestService.changeOrganizationPassword(dto.getOrganizationInn());
                boolean emailSent = emailService.sendSimpleMessageNoAsync(clsOrganization.getEmail(),
                        applicationConstants.getApplicationName() + ". Восстановление пароля",
                        "По ИНН " + clsOrganization.getInn() + " произведена смена пароля. " +
                                "Ваш новый пароль от личного кабинета на портале Работающая Бурятия: " + newPassword);
                if (!emailSent) {
                    return "Не удалось отправить письмо";
                }
                return "Ок";
            } else {
                return "Адрес электронной почты не привязан к учетной записи";
            }
        }
        return "ИНН не найден";
    }

    private boolean isEmailLinked(ClsOrganization organization, String email) throws Exception {
        boolean emailLinked = false;
        if (email.equals(organization.getEmail())) {
            emailLinked = true;
        } else {
            int typeOrganization = organization.getIdTypeOrganization().intValue();
            if (typeOrganization == OrganizationTypes.JURIDICAL.getValue() || typeOrganization == OrganizationTypes.FILIATION.getValue()
                    || typeOrganization == OrganizationTypes.REPRESENTATION.getValue() || typeOrganization == OrganizationTypes.DETACHED.getValue()) {
                RegEgrul egrul = egrulService.getEgrul(organization.getInn());
                if (egrul != null) {
                    EgrulResponse response = new EgrulResponse();
                    response.build(egrul);
                    if (response.getData().getEmail() != null && !response.getData().getEmail().isBlank()
                            && email.equals(response.getData().getEmail())) {
                        emailLinked = true;
                    }
                }
            } else if (typeOrganization == OrganizationTypes.IP.getValue() || typeOrganization == OrganizationTypes.KFH.getValue()) {
                List<RegEgrip> egrips = egrulService.getEgrip(organization.getInn());
                if (egrips != null && egrips.size() > 0) {
                    EgripResponse response = new EgripResponse();
                    response.build(egrips);
                    for (EgripResponse.Data data: response.getData()) {
                        if (data.getEmail() != null && !data.getEmail().isBlank()
                                && email.equals(data.getEmail())) {
                            emailLinked = true;
                        }
                    }
                }
            }
        }
        return emailLinked;
    }
}
