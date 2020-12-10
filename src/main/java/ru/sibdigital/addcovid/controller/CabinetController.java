package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.dto.ClsMailingListDto;
import ru.sibdigital.addcovid.dto.EmployeeDto;
import ru.sibdigital.addcovid.dto.OrganizationContactDto;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.dto.PrincipalDto;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.model.classifier.gov.Okved;
import ru.sibdigital.addcovid.repository.*;
import ru.sibdigital.addcovid.service.RequestService;

import javax.servlet.http.HttpSession;
import java.io.Console;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class CabinetController {

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    @Autowired
    private ClsPrincipalRepo clsPrincipalRepo;

    @Autowired
    private RequestService requestService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    private DocEmployeeRepo docEmployeeRepo;

    @Autowired
    private ClsNewsRepo clsNewsRepo;

    @Autowired
    private ClsMailingListRepo clsMailingListRepo;

    @Autowired
    private RegMailingListFollowerRepo regMailingListFollowerRepo;

    @Autowired
    private DocAddressFactRepo docAddressFactRepo;

    @Autowired
    private RegOrganizationAddressFactRepo regOrganizationAddressFactRepo;

    @Autowired
    private FiasAddrObjectRepo fiasAddrObjectRepo;

    @Autowired
    private ClsTypeRequestRepo clsTypeRequestRepo;

    @GetMapping("/cabinet")
    public String cabinet(HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User principal = (User) auth.getPrincipal();
        ClsOrganization organization = requestService.findOrganizationByInn(principal.getUsername());
        session.setAttribute("id_organization", organization.getId());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        model.addAttribute("id_organization", organization.getId());
        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
        return "cabinet/main";
    }

    @GetMapping("/organization")
    public @ResponseBody ClsOrganization getOrganization(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        return organization;
    }

    @GetMapping("/reg_organization_okved_add")
    public @ResponseBody
    List<Okved> getOrganizationNotMainOkveds(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        List<RegOrganizationOkved> regOrganizationOkveds = requestService.getRegOrganizationOkvedAddByIdOrganization(id);

        List<Okved> organizationOkveds = regOrganizationOkveds.stream().map((s) -> s.getRegOrganizationOkvedId().getOkved()).collect(Collectors.toList());

        return organizationOkveds;
    }

    @GetMapping("/reg_organization_okved")
    public @ResponseBody
    Okved getOrganizationMainOkved(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        Okved organizationOkved = null;
        RegOrganizationOkved regOrganizationOkved = requestService.getRegOrganizationOkvedByIdOrganization(id);
        if (regOrganizationOkved != null) {
            organizationOkved = regOrganizationOkved.getRegOrganizationOkvedId().getOkved();
        }

        return organizationOkved;
    }

    @GetMapping("/org_requests")
    public @ResponseBody List<DocRequest> getRequests(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<DocRequest> requests = docRequestRepo.getAllByInn(organization.getInn()).orElse(null);
        return requests;
    }

    @PostMapping("/save_pass")
    public @ResponseBody String savePassword(@RequestBody PrincipalDto principalDto, HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return "Пароль не обновлен";
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        if (organization != null && organization.getPrincipal() != null) {
            ClsPrincipal principal = organization.getPrincipal();
            principal.setPassword(passwordEncoder.encode(principalDto.getPassword()));
            clsPrincipalRepo.save(principal);
            return "Пароль обновлен";
        }
        return "Пароль не обновлен";
    }

    @GetMapping("/prescriptions")
    public @ResponseBody List<ClsTypeRequest> getPrescriptions(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        List<ClsTypeRequest> prescriptions = clsTypeRequestRepo.getPrescriptionsByOrganizationId(id);
        return prescriptions;
    }

    @PostMapping("/cabinet/typed_form")
    public @ResponseBody String postTypedForm(@RequestParam("request_type") Integer idTypeRequest,
                                              @RequestBody PostFormDto postFormDto) {

        try {
            ClsTypeRequest clsTypeRequest = requestService.getClsTypeRequestById(idTypeRequest);
            if (clsTypeRequest == null) {
                return "Данный тип заявки не существует!";
            } else if (clsTypeRequest.getStatusRegistration() != StatusRegistration.OPENED.getValue()) {
                return "Подача заявок с данным типом закрыта!";
            }
            //валидация
            String errors = validate(postFormDto);
            if(errors.isEmpty()){
                DocRequest docRequest = requestService.addNewRequest(postFormDto, idTypeRequest);
                return "Заявка принята. Ожидайте ответ на электронную почту.";
            }
            else {
                return errors;
            }
        } catch(Exception e){
            log.error(e.getMessage(), e);
            return "Невозможно сохранить заявку";
        }
    }

    private String validate(PostFormDto postFormDto) {

        String errors = "";
        try {

            if (postFormDto.getIsAgree() == false) {
                errors += "Необходимо подтвердить согласие работников на обработку персональных данных\n";
            }
            if (postFormDto.getIsProtect() == false) {
                errors += "Необходимо подтвердить обязательное выполнение предписания Управления Роспотребнадзора по Республике Бурятия\n";
            }
            if (postFormDto.getPersons() == null ||postFormDto.getPersons().isEmpty()) {
                errors += "Необходимо заполнить список работников\n";
            }
        }catch (Exception ex){
            log.error(ex.getMessage(), ex);
            errors += "Неправильно заполнены необходимые поля\n";
        }

        if (!errors.isEmpty()){
            errors = "ЗАЯВКА НЕ ВНЕСЕНА. ОБНАРУЖЕНЫ ОШИБКИ ЗАПОЛНЕНИЯ: " + errors;
        }

        return errors;
    }

    @PostMapping("/cabinet/personal_form")
    public @ResponseBody String postPersonalForm(@RequestBody PostFormDto postFormDto) {
        try {
            String errors = validatePersonalForm(postFormDto);
            if (errors.isEmpty()) {
                DocRequest docRequest = requestService.addPersonalRequest(postFormDto, RequestTypes.PERSONAL.getValue());
//                emailService.sendSimpleMessage(docRequest.getOrganization().getEmail(), applicationConstants.getApplicationName(), "Ваша заявка получена.");
                return "На указанный e-mail отправлено письмо!";
            } else {
                return errors;
            }
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return "Невозможно сохранить уведомление!";
        }
    }

    private String validatePersonalForm(PostFormDto postFormDto) {
        String errors = "";
        try {
            if (postFormDto.getIsAgree() == false) {
                errors += "Необходимо подтвердить согласие на обработку персональных данных\n";
            }
            if (postFormDto.getIsProtect() == false) {
                errors += "Необходимо подтвердить обязательное выполнение предписания Управления Роспотребнадзора по Республике Бурятия\n";
            }
            if (postFormDto.getAddressFact() == null || postFormDto.getAddressFact().isEmpty()) {
                errors += "Необходимо заполнить список адресов\n";
            }
            if (postFormDto.getTypeTaxReporting() == null) {
                errors += "Выберите способ сдачи налоговой отчетности";
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            errors += "Неправильно заполнены необходимые поля\n";
        }

        if (!errors.isEmpty()) {
            errors = "УВЕДОМЛЕНИЕ НЕ ПРИНЯТО. ОБНАРУЖЕНЫ ОШИБКИ ЗАПОЛНЕНИЯ: " + errors;
        }

        return errors;
    }

    @GetMapping("/org_contacts")
    public @ResponseBody List<ClsOrganizationContact> getOrgContacts(HttpSession session){
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<ClsOrganizationContact> organizationContacts = requestService.getAllClsOrganizationContactByOrganizationId(organization.getId());
        return organizationContacts;
    }

    @PostMapping("/save_contact")
    public @ResponseBody ClsOrganizationContact saveOrgContact(@RequestBody OrganizationContactDto organizationContactDto, HttpSession session){
        Long id = (Long) session.getAttribute("id_organization");
        organizationContactDto.setOrganizationId(id);
        ClsOrganizationContact clsOrganizationContact = null;
        try {
            clsOrganizationContact = requestService.saveOrgContact(organizationContactDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return clsOrganizationContact;
    }

    @PostMapping("/delete_org_contact")
    public @ResponseBody OrganizationContactDto deleteOrgContact(@RequestBody OrganizationContactDto organizationContactDto, HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        organizationContactDto.setOrganizationId(id);
        try{
            requestService.deleteOrgContact(organizationContactDto);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
        return organizationContactDto;
    }

    @GetMapping("/employees")
    public @ResponseBody List<DocEmployee> getEmployees(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<DocEmployee> employees = requestService.getEmployeesByOrganizationId(organization.getId());
        return employees;
    }

    @PostMapping("/filter")
    public @ResponseBody List<DocEmployee> getFilteredEmployees(@RequestBody String filterText, HttpSession session){
        String filter = filterText.trim().toLowerCase();
        return getEmployees(session).parallelStream()
                .filter(s -> containsFIO(s, filter))
                .collect(Collectors.toList());
    }

    private boolean containsFIO(DocEmployee employee, String filterText){
        String fio = constructFIO(employee.getPerson());
        return fio.contains(filterText);
    }

    private String constructFIO(DocPerson person){
        String fio = (person.getLastname() + person.getFirstname() + person.getPatronymic())
                .toLowerCase()
                .trim();
        return fio;
    }

    @PostMapping("/employee")
    public @ResponseBody DocEmployee saveEmployee(@RequestBody EmployeeDto employeeDto, HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        employeeDto.setOrganizationId(id);
        DocEmployee employee = null;
        try {
            employee = requestService.saveEmployee(employeeDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return employee;
    }

    @PostMapping("/deleteEmployee")
    public @ResponseBody EmployeeDto deleteEmployee(@RequestBody EmployeeDto employeeDto, HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        employeeDto.setOrganizationId(id);
        try{
            requestService.deleteEmployee(employeeDto);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
        return employeeDto;
    }

    @GetMapping("/newsfeed")
    public @ResponseBody List<ClsNews> getNewsList(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        List<ClsNews> newsList = clsNewsRepo.getNewsByOrganization_Id(id, new Timestamp(System.currentTimeMillis())).stream().collect(Collectors.toList());
        return newsList;
    }

    @GetMapping("/news/{id_news}")
    public @ResponseBody String getNewsById(@PathVariable("id_news") Long id_news){
        ClsNews clsNews = clsNewsRepo.findById(id_news).orElse(null);
        if (clsNews != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            String startTimeString = format.format(clsNews.getStartTime());
            String newsString = "<div><h3 style=\"color: #2e6c80;\">" + clsNews.getHeading()  + "</h3>" + clsNews.getMessage() + "</div>"+
                    "<div style='text-align:right;'>Дата публикации: " + startTimeString+ "</div></div>";
            return newsString;
        }
        else
            return "";
    }

    @GetMapping("/my_mailing_list")
    public @ResponseBody List<ClsMailingList> getMyMailingList(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        if (organization != null && organization.getPrincipal() != null) {
            ClsPrincipal principal = organization.getPrincipal();
            List<ClsMailingList> mailingList = clsMailingListRepo.findMyMailingList(principal.getId());
            return mailingList;
        }
        else {
            return null;
        }
    }

    @GetMapping("/available_not_mine_mailing_list")
    public @ResponseBody List<ClsMailingList> getAvailableMailingList(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        if (organization != null && organization.getPrincipal() != null) {
            ClsPrincipal principal = organization.getPrincipal();
            List<ClsMailingList> mailingList = clsMailingListRepo.findAvailableMailingListNotMine(principal.getId());
            return mailingList;
        }
        else {
            return null;
        }
    }

    @PostMapping("/saveMailing")
    public @ResponseBody
    String saveMailing(@RequestBody List<ClsMailingListDto> clsMailingListDtos, HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        if (organization != null && organization.getPrincipal() != null) {
            ClsPrincipal principal = organization.getPrincipal();
            for (ClsMailingListDto clsMailingListDto : clsMailingListDtos) {
                try {
                    requestService.saveMailing(clsMailingListDto, principal);
                }
                catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return null;
                }
            }
        }
        return "Рассылки сохранены";
    }


    @PostMapping("/deactivateMailing")
    public @ResponseBody
    String deactivateMailing(@RequestBody List<ClsMailingListDto> clsMailingListDtos, HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        if (organization != null && organization.getPrincipal() != null) {
            ClsPrincipal principal = organization.getPrincipal();
            for (ClsMailingListDto clsMailingListDto : clsMailingListDtos) {
                try {
                    requestService.deactivateMailing(clsMailingListDto, principal);
                }
                catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return "Не получилось деактивировать рассылку: " + clsMailingListDto.getName();
                }
            }
        }
        return "Рассылки деактивированы";
    }

    @PostMapping("/cabinet/new_request")
    public @ResponseBody String postNewRequest(@RequestBody PostFormDto postFormDto) {
        try {
            String errors = validateNewRequest(postFormDto);
            if (errors.isEmpty()) {
                requestService.saveNewRequest(postFormDto);
                return "Заявка принята. Ожидайте ответ на электронную почту.";
            } else {
                return errors;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Невозможно подать заявку";
        }
    }

    private String validateNewRequest(PostFormDto postFormDto) {
        String errors = "";
        try {
            if (postFormDto.getIsAgree() == false) {
                errors += "Необходимо подтвердить согласие работников на обработку персональных данных\n";
            }
//            if (postFormDto.getIsProtect() == false) {
//                errors += "Необходимо подтвердить обязательное выполнение предписания Управления Роспотребнадзора по Республике Бурятия\n";
//            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            errors += "Неправильно заполнены необходимые поля\n";
        }

        if (!errors.isEmpty()) {
            errors = "ЗАЯВКА НЕ ВНЕСЕНА. ОБНАРУЖЕНЫ ОШИБКИ ЗАПОЛНЕНИЯ: " + errors;
        }

        return errors;
    }

    @GetMapping("/address_facts")
    public @ResponseBody  List<Map<String, Object>> getAddressFactsList(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        List<Map<String, Object>> result = regOrganizationAddressFactRepo.findByIdOrganization(Integer.parseInt(id.toString()));
        return result;
    }

    @PostMapping("/save_address_fact")
    public @ResponseBody RegOrganizationAddressFact saveRegAddressFact(@RequestBody RegOrganizationAddressFact regOrganizationAddressFact, HttpSession session){
        Long id = (Long) session.getAttribute("id_organization");
        RegOrganizationAddressFact _regOrganizationAddressFact = null;
        try {
            _regOrganizationAddressFact = requestService.saveRegOrgAddressFact(regOrganizationAddressFact, id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return _regOrganizationAddressFact;
    }

    @PostMapping("/delete_address_fact")
    public @ResponseBody void deleteRegAddressFact(@RequestBody RegOrganizationAddressFact regOrganizationAddressFact){
        try {
            requestService.deleteRegOrgAddressFact(regOrganizationAddressFact);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @GetMapping("/regions")
    public @ResponseBody List<Map<String, Object>> getRegions() {
        List<Map<String, Object>> result = fiasAddrObjectRepo.findRegions();
        return result;
    }

    @GetMapping("/raions")
    public @ResponseBody List<Map<String, Object>> getRaions(
            @RequestParam(value = "objectid", required = false) Long objectId
    ) {
        List<Map<String, Object>> result = null;
        if (objectId != null) {
            result = fiasAddrObjectRepo.findRaions(objectId);
            Map<String, Object> notSelect = new HashMap<>();
            notSelect.put("value", (Object) "<Не выбрано>");
            notSelect.put("typename", (Object) "");
            result.add(0, notSelect);
        }

        return result;
    }


    @GetMapping("/cities")
    public @ResponseBody List<Map<String, Object>> getCities(
            @RequestParam(value = "objectid", required = false) Long objectId
    ) {
        List<Map<String, Object>> result = null;
        if (objectId != null) {
            result = fiasAddrObjectRepo.findCities(objectId);
        }

        return result;
    }

    @GetMapping("/streets")
    public @ResponseBody List<Map<String, Object>> getStreets(
            @RequestParam(value = "objectid", required = false) Long raionObjectId
    ) {
        List<Map<String, Object>> result = null;
        if (raionObjectId != null) {
            result = fiasAddrObjectRepo.findStreetsByRaionOrCity(raionObjectId);
        }

        return result;
    }

    @GetMapping("/house")
    public @ResponseBody List<Map<String, Object>> getHome(
            @RequestParam(value = "objectid", required = false) Long streetObjectId
    ) {
        List<Map<String, Object>> result = null;
        if (streetObjectId != null) {
            result = fiasAddrObjectRepo.findHouseByStreet(streetObjectId);
        }

        return result;
    }
}
