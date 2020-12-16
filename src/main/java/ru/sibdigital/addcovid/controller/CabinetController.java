package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.dto.*;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.model.classifier.gov.Okved;
import ru.sibdigital.addcovid.repository.*;
import ru.sibdigital.addcovid.service.RequestService;
import ru.sibdigital.addcovid.service.SettingServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.sql.Timestamp;

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
    private ClsPrescriptionRepo clsPrescriptionRepo;

    @Autowired
    private SettingServiceImpl settingServiceImpl;

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

    @GetMapping("/count_confirmed_requests")
    public @ResponseBody List<DocRequest> getAllConfirmedRequest(HttpSession session){
        Long id = (Long) session.getAttribute("id_organization");
        if(id == null){
            return null;
        }
        List<DocRequest> confirmedRequestStatus = docRequestRepo.getAllRequestWithConfirmedStatus(id).orElse(null);
        return confirmedRequestStatus;
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
    public @ResponseBody List<ClsPrescriptionDto> getPrescriptions(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        List<ClsPrescriptionDto> prescriptions = requestService.getClsPrescriptionsByOrganizationId(id);
        return prescriptions;
    }

    @GetMapping("/prescription")
    public @ResponseBody ClsPrescription getClsPrescription(@RequestParam Long id) {
        return requestService.getClsPrescriptionById(id);
    }

    @PostMapping("/cabinet/organization_prescription")
    public @ResponseBody String postOrganizationPrescriptionForm(@RequestBody OrganizationPrescriptionDto dto, HttpSession session) {
        try {
            Long id = (Long) session.getAttribute("id_organization");
            if (id == null) {
                return null;
            }
            RegOrganizationPrescription organizationPrescription = requestService.addOrganizationPrescription(id, dto);
            return "Предписание сохранено";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось сохранить предписание";
        }
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
        List<ClsNews> newsList = clsNewsRepo.getCurrentNewsByOrganization_Id(id, new Timestamp(System.currentTimeMillis())).stream().collect(Collectors.toList());
        return newsList;
    }

    @GetMapping("/news_archive")
    public @ResponseBody Map<String, Object> getNewsArchive(HttpSession session, @RequestParam(value = "start", required = false) Integer start,
                                                         @RequestParam(value = "count", required = false) Integer count) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }

        int page = start == null ? 0 : start / 10;
        int size = count == null ? 10 : count;
        Map<String, Object> result = new HashMap<>();
        Page<ClsNews> templates = requestService.findNewsArchiveByOrganization_Id(id, page, size);

        result.put("data", templates.getContent());
        result.put("pos", (long) page * size);
        result.put("total_count", templates.getTotalElements());
        return result;
    }


    @GetMapping("/news")
    public String getNewsById( @RequestParam("hash_id") String hash_id, Model model){
        model.addAttribute("hash_id", hash_id);
        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "news_form";
    }

    @GetMapping("/news/{hash_id}")
    public @ResponseBody ClsNews getNewsById(@PathVariable("hash_id") String hash_id, HttpServletRequest request){
        ClsNews clsNews = clsNewsRepo.findByHashId(hash_id);
        requestService.saveLinkClicks(request, clsNews);
        return clsNews;
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

    @GetMapping("/raion")
    public @ResponseBody Map<String, Object> getRaionIdByObjectId(@RequestParam(value = "objectid", required = true) Long objectId) {
        Map<String, Object> result = null;
        if (objectId != null) {
            result = fiasAddrObjectRepo.findByObjectId(objectId);
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

    @GetMapping("/city")
    public @ResponseBody Map<String, Object> getCityByObjectId(@RequestParam(value = "objectid", required = true) Long objectId) {
        Map<String, Object> id = null;
        if (objectId != null) {
            id = fiasAddrObjectRepo.findByObjectId(objectId);
        }
        return id;
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

    @GetMapping("/street")
    public @ResponseBody Map<String, Object> getStreetByObjectId(@RequestParam(value = "objectid", required = true) Long objectId) {
        Map<String, Object> id = null;
        if (objectId != null) {
            id = fiasAddrObjectRepo.findByObjectId(objectId);
        }
        return id;
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

    @GetMapping("/requests_status_style")
    public @ResponseBody String getRequestsStatusStyle(){
        ClsSettings settings = settingServiceImpl.getRequestsStatusStyle();
        return settings.getValue();
    }
}
