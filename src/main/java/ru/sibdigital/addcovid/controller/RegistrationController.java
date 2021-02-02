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
import ru.sibdigital.addcovid.model.RegOrganizationClassifier;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrip;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrul;
import ru.sibdigital.addcovid.repository.specification.ClsOrganizationSearchCriteria;
import ru.sibdigital.addcovid.service.EmailService;
import ru.sibdigital.addcovid.service.RequestService;
import ru.sibdigital.addcovid.service.SettingService;
import ru.sibdigital.addcovid.service.crassifier.EgrulService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @PostMapping("/registration")
    public @ResponseBody String postRegistration(@RequestBody OrganizationDto organizationDto) {
        try {
            List<ClsOrganization> organizations = findOrganizationsByInn(organizationDto.getOrganizationInn());
            ClsOrganization clsOrganization = findExistOrganization(organizationDto, organizations);
            if (clsOrganization != null) {
                return "Данная организация уже зарегистрирована";
            }
            // проверим email, если это подразделение (филиал, представительство и т.п.)
            int typeOrganization = organizationDto.getOrganizationType();
            if (typeOrganization == OrganizationTypes.FILIATION.getValue() || typeOrganization == OrganizationTypes.REPRESENTATION.getValue()
                    || typeOrganization == OrganizationTypes.DETACHED.getValue() || typeOrganization == OrganizationTypes.KFH.getValue()) {
                clsOrganization = findOrganizationByEmail(organizationDto.getOrganizationEmail(), OrganizationTypes.FILIATION.getValue(),
                        OrganizationTypes.REPRESENTATION.getValue(), OrganizationTypes.DETACHED.getValue(), OrganizationTypes.KFH.getValue());
                if (clsOrganization != null) {
                    return "Указанный адрес электронной почты уже привязан к другой организации";
                }
            }
            // сохраним организацию
            clsOrganization = requestService.saveClsOrganization(organizationDto);
            // отправим письмо со ссылкой на активацию
            String activateUrl = settingService.findActualByKey("activationUrl", "");
            activateUrl += "/activate?inn=" + clsOrganization.getInn() + "&code=" + clsOrganization.getHashCode();
            String text = "Здравствуйте! <br/>" +
                    "Для активации учетной записи " + clsOrganization.getName()+ " в личном кабинете на портале " +
                    applicationConstants.getApplicationName() + " перейдите по ссылке: <a href=\"" + activateUrl + "\">Активация учетной записи</a><br/>" +
                    "Если вы не регистрировали личный кабинет на портале " +
                    applicationConstants.getApplicationName() + ", проигнорируйте это сообщение";
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

    private ClsOrganization findOrganizationByEmail(String email, Integer... types) {
        ClsOrganizationSearchCriteria searchCriteria = new ClsOrganizationSearchCriteria();
        searchCriteria.setEmail(email);
        searchCriteria.setTypeOrganizations(Arrays.asList(types));

        ClsOrganization organization = requestService.findOrganizations(searchCriteria).stream().findFirst().orElse(null);

        return organization;
    }

    private ClsOrganization findExistOrganization(OrganizationDto dto, List<ClsOrganization> organizations) {
        if (organizations == null) {
            return null;
        }
        ClsOrganization clsOrganization = null;
        for (ClsOrganization organization: organizations) {
            RegOrganizationClassifier regOrganizationClassifier = organization.getRegOrganizationClassifier();
            if (regOrganizationClassifier == null) {
                return organization;
            }
            if (!Objects.equals(organization.getIdTypeOrganization(), dto.getOrganizationType())) {
                continue;
            }
            Long regEgrulId = regOrganizationClassifier.getRegEgrul() == null ? null : regOrganizationClassifier.getRegEgrul().getId();
            if (!Objects.equals(regEgrulId, dto.getEgrulId())) {
                continue;
            }
            Long regEgripId = regOrganizationClassifier.getRegEgrip() == null ? null : regOrganizationClassifier.getRegEgrip().getId();
            if (!Objects.equals(regEgripId, dto.getEgripId())) {
                continue;
            }
            Long regFilialId = regOrganizationClassifier.getRegFilial() == null ? null : regOrganizationClassifier.getRegFilial().getId();
            if (!Objects.equals(regFilialId, dto.getFilialId())) {
                continue;
            }
            clsOrganization = organization;
            break;
        }
        return clsOrganization;
    }

    private List<ClsOrganization> findOrganizationsByInn(String inn) {
        ClsOrganizationSearchCriteria searchCriteria = new ClsOrganizationSearchCriteria();
        searchCriteria.setInn(inn);

        return requestService.findOrganizations(searchCriteria);
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
    public @ResponseBody String recovery(@RequestBody OrganizationDto organizationDto) {
        List<ClsOrganization> clsOrganizations = findOrganizationsByInn(organizationDto.getOrganizationInn()).stream()
                .filter(org -> org.getActivated()).collect(Collectors.toList());
        if (clsOrganizations != null && clsOrganizations.size() > 0) {
            ClsOrganization organization = null;
            for (ClsOrganization clsOrganization: clsOrganizations) {
                boolean emailLinked = false;
                try {
                    emailLinked = isEmailLinked(clsOrganization, organizationDto.getOrganizationEmail());
                } catch (Exception e) {
                    log.info(e.getMessage(), e);
                }
                if (emailLinked) {
                    organization = clsOrganization;
                    break;
                }
            }
            if (organization != null) {
                String newPassword = requestService.changeOrganizationPassword(organization);
                boolean emailSent = emailService.sendSimpleMessageNoAsync(organization.getEmail(),
                        applicationConstants.getApplicationName() + ". Восстановление пароля",
                        "По ИНН " + organization.getInn() + " произведена смена пароля. " +
                                "Ваш новый пароль от личного кабинета на портале " + applicationConstants.getApplicationName() + ":" + newPassword);
                if (!emailSent) {
                    return "Не удалось отправить письмо";
                }
                return "Ок";
            } else {
                return "Адрес электронной почты не привязан к учетной записи";
            }
        }
        return "Организация не найдена";
    }

    private boolean isEmailLinked(ClsOrganization organization, String email) {
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
