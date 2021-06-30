package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.dto.*;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.model.classifier.gov.Okved;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrip;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrul;
import ru.sibdigital.addcovid.repository.*;
import ru.sibdigital.addcovid.service.OrganizationService;
import ru.sibdigital.addcovid.service.RequestService;
import ru.sibdigital.addcovid.service.SettingServiceImpl;
import ru.sibdigital.addcovid.service.crassifier.EgrulService;

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

    @Autowired
    private RegNewsFileRepo regNewsFileRepo;

    @Autowired
    private RegPersonCountRepo regPersonCountRepo;

    @Autowired
    private ClsDepartmentRepo clsDepartmentRepo;

    @Autowired
    private ClsDepartmentContactRepo clsDepartmentContactRepo;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private EgrulService egrulService;

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
    }

    @GetMapping("/cabinet")
    public String cabinet(Authentication authentication, HttpSession session) {
        Long id;
        if (authentication.getPrincipal() instanceof OidcUser) {
            id = (Long) session.getAttribute("id_organization");
            if (id == null) {
                return "redirect:/esia";
            }
        } else {
            User principal = (User) authentication.getPrincipal();
            id = Long.valueOf(principal.getUsername());
            session.setAttribute("id_organization", id);
        }
        return "cabinet/main";
    }

    @GetMapping("/esia")
    public String esia(Model model, HttpSession session) {
        try {
            List<OrganizationDto> organizations = organizationService.getOrganizationsByEsia();
            if (organizations.isEmpty()) {
                return "redirect:/logout";
            }
            if (organizations.size() == 1) {
                OrganizationDto esiaOrg = organizations.get(0);
                ClsOrganization organization = organizationService.findByInnAndPrincipalIsNotNull(esiaOrg.getOrganizationInn(),
                        Arrays.asList(esiaOrg.getOrganizationType()));
                if (organization == null) {
                    organization = organizationService.saveNewClsOrganizationAsActivated(esiaOrg);
                }
                session.setAttribute("id_organization", organization.getId());
                return "redirect:/cabinet";
            }
            model.addAttribute("organizations", organizations);
            session.setAttribute("organizations", organizations);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "redirect:/logout";
        }
        return "cabinet/esia";
    }

    @GetMapping("/esia/{id}")
    public String esia(@PathVariable(name = "id") Long id,
                       @SessionAttribute(name = "id_organization", required = false) Long idOrganization,
                       @SessionAttribute(name = "organizations") List<OrganizationDto> organizations,
                       HttpSession session) {
        if (idOrganization != null) {
            return "redirect:/cabinet";
        }
        try {
            OrganizationDto esiaOrg = organizations.stream().filter(o -> o.getEsiaId().equals(id)).findFirst().get();
            ClsOrganization organization = organizationService.findByInnAndPrincipalIsNotNull(esiaOrg.getOrganizationInn(),
                    Arrays.asList(esiaOrg.getOrganizationType()));
            if (organization == null) {
                organization = organizationService.saveNewClsOrganizationAsActivated(esiaOrg);
            }
            session.setAttribute("id_organization", organization.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "redirect:/logout";
        }
        return "redirect:/cabinet";
    }

    @GetMapping("/check_consent")
    public @ResponseBody Map<String, Object> checkConsent(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        boolean isAgreed = ((organization.getConsentDataProcessing() == null) ? false : organization.getConsentDataProcessing());

        Map<String, Object> map = new HashMap<>();
        map.put("isAgreed", isAgreed);

        return map;
    }

    @GetMapping("/getConsentPersonalData")
    public @ResponseBody String getConsentPersonalData() {
        ClsSettings settings = settingServiceImpl.getConsentPersonalData();
        return settings != null ? settings.getStringValue() : "";
    }

    @GetMapping("/saveConsentPersonalData")
    public @ResponseBody String saveConsentPersonalData(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return "Не удалось сохранить согласие";
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        organization.setConsentDataProcessing(true);
        clsOrganizationRepo.save(organization);
        return "Согласие сохранено";
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
    public @ResponseBody List<DocRequestDto> getRequests(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<DocRequest> requests = docRequestRepo.getRequestsByOrganizationIdAndStatusActivity(organization.getId(),
                ActivityStatuses.ACTIVE.getValue()).orElse(new ArrayList<>());
        List<DocRequestDto> dtos = requests.stream().map(request -> new DocRequestDto(request.getId(),
                request.getTypeRequest().getActivityKind(), request.getStatusReview(), request.getStatusReviewName(),
                request.getDepartment().getName(), request.getTimeCreate(), request.getTimeReview(),
                request.getTypeRequest().getId()))
                .collect(Collectors.toList());
        return dtos;
    }

    @GetMapping("/org_requests/{id}")
    public @ResponseBody DocRequest getDocRequest(@PathVariable("id") Long idRequest, HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        DocRequest request = docRequestRepo.findById(idRequest).orElse(null);
        if (request != null && request.getOrganization().getId().equals(id)) {
            return request;
        }
        return null;
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

    @GetMapping("/check_existence_request")
    public @ResponseBody Map getRequestByType(@RequestParam("id") Long typeRequestId, HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        Long requestId = requestService.getRequestByOrganizationIdAndTypeRequestId(id, typeRequestId, ReviewStatuses.NEW.getValue());
        if (requestId != null) {
            return Map.of("id", requestId, "status", ReviewStatuses.NEW.getValue());
        }
        requestId = requestService.getRequestByOrganizationIdAndTypeRequestId(id, typeRequestId, ReviewStatuses.OPENED.getValue());
        if (requestId != null) {
            return Map.of("id", requestId, "status", ReviewStatuses.OPENED.getValue());
        }
        requestId = requestService.getRequestByOrganizationIdAndTypeRequestId(id, typeRequestId, ReviewStatuses.CONFIRMED.getValue());
        if (requestId != null) {
            return Map.of("id", requestId, "status", ReviewStatuses.CONFIRMED.getValue());
        }
        requestId = requestService.getRequestByOrganizationIdAndTypeRequestId(id, typeRequestId, ReviewStatuses.REJECTED.getValue());
        if (requestId != null) {
            return Map.of("id", requestId, "status", ReviewStatuses.REJECTED.getValue());
        }
        return Map.of("id", 0);
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
            if (principalDto.getPassword() != null && !principalDto.getPassword().isBlank()) {
                principal.setPassword(passwordEncoder.encode(principalDto.getPassword()));
            }else{
                return "Невозможно установить пустой пароль!";
            }
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

    @GetMapping("/count_non_consent_prescriptions")
    public @ResponseBody Map<String, Integer> getAllNonConsentPrescription(HttpSession session){
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null){
            return Map.of("count", 0);
        }
        Integer count = requestService.getCountOfNewClsPrescriptionsByOrgId(id);
        return Map.of("count", count);
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

    @GetMapping("/type_request_prescriptions")
    public @ResponseBody List<ClsPrescription> getTypeRequestPrescriptions(@RequestParam Long idTypeRequest) {
        return requestService.getClsPrescriptionsByTypeRequestId(idTypeRequest);
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
        List<DocEmployee> employees = new ArrayList<>();
        if (id == null) {
            return employees;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        employees = requestService.getEmployeesByOrganizationIdAndIsDeletedStatus(organization.getId());
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
    public @ResponseBody List<Map<String, Object>> getNewsList(HttpSession session) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        List<ClsNews> newsList = clsNewsRepo.getCurrentNewsByOrganization_Id(id, new Timestamp(System.currentTimeMillis())).stream().collect(Collectors.toList());
        for (ClsNews news : newsList) {
            Map<String, Object> map = new HashMap<>();
            List<RegNewsFile> newsFiles = regNewsFileRepo.findAllByNews(news);

            map.put("news", news);
            map.put("newsFiles", newsFiles);
            map.put("newsDirectory", applicationConstants.getNewsUploadPath());
            mapList.add(map);
        }

        return mapList;
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
        return "news_form";
    }

    @GetMapping("/newsform/{hash_id}")
    public @ResponseBody Map<String, Object> getNewsDataById(@PathVariable("hash_id") String hash_id, HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();

        ClsNews news = clsNewsRepo.findByHashId(hash_id);
        List<RegNewsFile> newsFiles = regNewsFileRepo.findAllByNews(news);

        map.put("news", news);
        map.put("newsFiles", newsFiles);
        map.put("newsDirectory", applicationConstants.getNewsUploadPath());

        requestService.saveLinkClicks(request, news);
        return map;
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
    public @ResponseBody String postNewRequest(@RequestBody PostFormDto postFormDto, HttpSession session) {
        try {
            Long id = (Long) session.getAttribute("id_organization");
            if (id == null) {
                return "Ид организации не указан";
            }

            postFormDto.setOrganizationId(id);

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
//            if (postFormDto.getIsAgree() == false) {
//                errors += "Необходимо подтвердить согласие работников на обработку персональных данных\n";
//            }
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
            @RequestParam(value = "objectid", required = false) Long objectId,
            @RequestParam(value = "regionCode", required = false) Short regionCode,
            @RequestParam(value = "name", required = false) String name
    ) {
        List<Map<String, Object>> result = null;
        if (regionCode != null && name == null) {
            result = fiasAddrObjectRepo.findCities(regionCode);
        } else if(name != null && regionCode != null) {
            String parseName = "^" + name + "+";
            result = fiasAddrObjectRepo.findCitiesWithFilter(regionCode, parseName);
            System.out.println(parseName);
        }

        return result;
    }

    @GetMapping("/city")
    public @ResponseBody Map<String, Object> getCityByObjectId(@RequestParam(value = "objectid", required = true) Long objectId) {
        Map<String, Object> id = null;
        if (objectId != null) {
            id = fiasAddrObjectRepo.findCityByObjectId(objectId);
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


    @GetMapping("/person_count")
    public @ResponseBody RegPersonCount savePersonCount(HttpSession session){
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        RegPersonCount rpc = regPersonCountRepo.getLastPersonCntByOrganization_Id(id);
        return rpc;
    }

    @GetMapping("/save_person_count")
    public @ResponseBody String savePersonCount(@RequestParam(value = "personOfficeCnt") Integer personOfficeCnt,
                                                        @RequestParam(value = "personRemoteCnt") Integer personRemoteCnt,
                                                        HttpSession session){
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        try {
            requestService.saveRegPersonCount(organization, personOfficeCnt, personRemoteCnt);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Не удалось внести изменения";
        }
        return "Изменения сохранены";
    }

    @GetMapping("/dep_contacts")
    public @ResponseBody List<Map<String, Object>> getDepartmentContacts() {
        List<ClsDepartment> departments = clsDepartmentRepo.findByIsDeletedFalseOrderByIdAsc();
        List<Map<String, Object>> list = new ArrayList<>();
        for (ClsDepartment department : departments) {
            Map<String, Object> map = new HashMap<>();
            List<ClsDepartmentContact> phones = clsDepartmentContactRepo.findAllByDepartmentAndType(department, 1);
            List<ClsDepartmentContact> emails = clsDepartmentContactRepo.findAllByDepartmentAndType(department, 0);
            map.put("department", department);
            map.put("phones", phones);
            map.put("emails", emails);

            list.add(map);
        }
        return list;
    }

    @PostMapping("/check_current_pass")
    public @ResponseBody String checkCurrentPass(@RequestBody String incomingPass, HttpSession session){
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return "Не удалось получить данные организации";
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        String currentPass = organization.getPrincipal().getPassword();
        if(passwordEncoder.matches(incomingPass,currentPass)){
            return "Пароли совпадают";
        }else{
            return "Пароли не совпадают";
        }
    }

    @PostMapping("/edit_common_info")
    public @ResponseBody
    ResponseEntity<Object>  editCommonInfo(@RequestBody OrganizationCommonInfoDto organizationCommonInfoDto, HttpSession session){
        ResponseEntity<Object> responseEntity = null;
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
           return null;
        }

        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        String email = organizationCommonInfoDto.getOrganizationEmail().trim();
        String phone = organizationCommonInfoDto.getOrganizationPhone().trim();
        if (organization != null && email != null && phone != null) {
            organization.setEmail(email);
            organization.setPhone(phone);
            clsOrganizationRepo.save(organization);
        }

        String shortOrganizationName = organizationCommonInfoDto.getShortOrganizationName();
        String organizationName = organizationCommonInfoDto.getOrganizationName();
        String addressJur = organizationCommonInfoDto.getAddressJur();

        if (shortOrganizationName != null && shortOrganizationName.isBlank() == false
                && organization.getShortName().equals(shortOrganizationName.trim()) == false){
            organization.setShortName(shortOrganizationName.trim());
            clsOrganizationRepo.save(organization);
        }

        if (organizationName != null && organizationName.isBlank() == false
                && organization.getName().equals(organizationName.trim()) == false){
            organization.setName(organizationName.trim());
            clsOrganizationRepo.save(organization);
        }

        if (addressJur != null && addressJur.isBlank() == false
                && organization.getAddressJur().equals(addressJur.trim()) == false){
            organization.setAddressJur(addressJur.trim());
            clsOrganizationRepo.save(organization);
        }

            String newPass = organizationCommonInfoDto.getNewPass();
        if(!newPass.isBlank()){
            PrincipalDto principalDto = new PrincipalDto();
            principalDto.setPassword(newPass);
            savePassword(principalDto,session);
        }
        responseEntity = ResponseEntity.ok()
                .body("{\"cause\": \"Данные успешно обновлены\"," +
                        "\"status\": \"server\"}");
        return responseEntity;
    }

    @GetMapping("/update_org_by_egrul")
    public @ResponseBody ClsOrganization updateOrganizationByEgrul(@RequestParam ("inn") String inn, HttpSession session){
        Long id_organization = (Long) session.getAttribute("id_organization");
        if (id_organization == null) {
            return null;
        }

        EgrulResponse egrulResponse = new EgrulResponse();
        EgripResponse egripResponse = new EgripResponse();
        ClsOrganization clsOrganization = null;

        RegEgrul regEgrul = egrulService.getEgrul(inn);
        if(regEgrul != null) {
            egrulResponse.build(regEgrul);
            if(egrulResponse.isFinded()){
                EgrulResponse.Data data = egrulResponse.getData();
                clsOrganization = organizationService.updateClsOrganizationByEgrul(data, id_organization);
            }
        }else{
            final List<RegEgrip> egripList = egrulService.getEgrip(inn);

            if (egripList.isEmpty() == false){
                egripResponse.build(egripList);

                if(egripResponse.isFinded()){
                    List<EgripResponse.Data> dataList = egripResponse.getData();
                    clsOrganization = organizationService.updateClsOrganizationByEgrip(dataList.get(0), id_organization);
                }
            }else {
                egrulResponse.empty("По введенному ИНН не найдено юр. лицо");
            }
        }

        return clsOrganization;
    }

    @GetMapping("/update_org_type")
    public @ResponseBody ClsOrganization updateOrganizationType(@RequestParam ("type") int type, HttpSession session){
        Long id_organization = (Long) session.getAttribute("id_organization");
        if (id_organization == null) { return null; }

        ClsOrganization clsOrganization = clsOrganizationRepo.findById(id_organization).orElse(null);
        clsOrganization.setIdTypeOrganization(type);
        clsOrganizationRepo.save(clsOrganization);
        return clsOrganization;
    }

    @GetMapping("/org_types")
    public @ResponseBody List<Integer> getOrganizationTypes(){
        List<Integer> types = Arrays.asList(
                OrganizationTypes.SELF_EMPLOYED.getValue(),
                OrganizationTypes.IP.getValue(),
                OrganizationTypes.KFH.getValue()
                );
        return types;
    }
}
