package ru.sibdigital.addcovid.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sibdigital.addcovid.dto.DachaDto;
import ru.sibdigital.addcovid.dto.ListItemDto;
import ru.sibdigital.addcovid.dto.PostFormDto;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.repository.ClsDepartmentRepo;
import ru.sibdigital.addcovid.service.DachaService;
import ru.sibdigital.addcovid.service.EmailService;
import ru.sibdigital.addcovid.service.RequestService;

import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private DachaService dachaService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String greeting(Map<String, Object> model) throws JsonProcessingException {

        List<ListItemDto> listDepartment =  clsDepartmentRepo.findAll()
                .stream()
                .map(clsDepartment -> new ListItemDto(Long.valueOf(clsDepartment.getId()), clsDepartment.getName()))
                .collect(Collectors.toList());

        model.put("listDepartment", listDepartment);
        
        return "index";
    }

    @PostMapping(value = "/uploadpart", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadFile(@RequestParam(value = "upload") MultipartFile file){
        return ResponseEntity.ok().body(requestService.uploadFile(file));
    }

    @GetMapping(value = "/download/{id}")
    public void downloadFile(HttpServletResponse response, @PathVariable("id") DocRequest docRequest) throws Exception {
        requestService.downloadFile(response, docRequest);
    }

    private String addError(String field, String msg){
        return "{'field': '" + field + "', 'msg': '" + msg + "'}";
    }

    private String validate(PostFormDto postFormDto){

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
            if(postFormDto.getOrganizationInn() == null || postFormDto.getOrganizationInn().isEmpty() ){
                errors +=  "Заполните ИНН";
            }else if(postFormDto.getOrganizationInn().length() > 12 ){
                errors +=  "Превышена длина ИНН";
            }
            if (!postFormDto.getIsSelfEmployed()) {
                if(postFormDto.getOrganizationOgrn() == null || postFormDto.getOrganizationOgrn().length() > 15 ){
                    errors +=  "Превышена длина 'Превышена длина ОГРН'";
                }
            }
            if(postFormDto.getOrganizationPhone() == null || postFormDto.getOrganizationPhone().length() > 100){
                errors +=  "Превышена длина номера телефона";
            }
            if(postFormDto.getOrganizationEmail() == null || postFormDto.getOrganizationEmail().length() > 100){
                errors +=  "Превышена длина электронной почты";
            }
            if(postFormDto.getOrganizationShortName() == null || postFormDto.getOrganizationShortName().length() > 255){
                errors +=  "Превышена длина краткого наименования";
            }
            if(postFormDto.getOrganizationAddressJur() == null ||postFormDto.getOrganizationAddressJur().length() > 255){
                errors +=  "Превышена длина юридического адреса";
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

    @GetMapping("/dacha")
    public String dacha(Map<String, Object> model) throws JsonProcessingException {
        return "dacha";
    }

    @PostMapping("/dacha")
    public ResponseEntity<String> dachaPostForm(@RequestBody DachaDto dachaDto) {
        try {
            DocDacha docDacha = dachaService.addNewRequest(dachaDto);
            return ResponseEntity.ok().body("<b>Желаем Вам счастливого пути!</b><br/>" +
                    "Напоминаем о необходимости иметь при себе паспорт и документы, подтверждающие право собственности или владения недвижимостью!");
        } catch(Exception e){
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Невозможно сохранить заявку");
        }
    }

    @GetMapping("/barber")
    public String barber(Map<String, Object> model) throws JsonProcessingException {
        return "barber";
    }

    @GetMapping("/form")
    public String form(Map<String, Object> model) throws JsonProcessingException {
        return "form";
    }

    @PostMapping("/form")
    public @ResponseBody
    String postForm(@RequestBody PostFormDto postFormDto) {

        try {
            //валидация
            String errors = validate(postFormDto);
            if(errors.isEmpty()){
                DocRequest docRequest = requestService.addNewRequest(postFormDto, RequestTypes.ORGANIZATION.getValue());

//            return hash;
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

    @PostMapping("/barber")
    public @ResponseBody
    String postBarbershopForm(@RequestBody PostFormDto postFormDto) {

        try {
            //валидация
            String errors = validate(postFormDto);
            if(errors.isEmpty()){
                DocRequest docRequest = requestService.addNewRequest(postFormDto, RequestTypes.BARBERSHOP.getValue());

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

    @GetMapping("/typed_form")
    public String typedForm(@RequestParam(name = "request_type") Long idTypeRequest, Model model) {
        model.addAttribute("idTypeRequest", idTypeRequest);
        return "typed_form";
    }

    @GetMapping("/cls_type_request/{request_type}")
    public @ResponseBody ClsTypeRequest getClsTypeRequest(@PathVariable(name = "request_type") Integer idTypeRequest) {
        return requestService.getClsTypeRequestById(idTypeRequest);
    }

    @GetMapping("/cls_type_requests")
    public @ResponseBody List<ClsTypeRequest> getClsTypeRequests() {
        return requestService.getClsTypeRequests();
    }

    @PostMapping("/typed_form")
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

    @GetMapping("/personal_form")
    public String personalForm(Model model) {
        return "personal_form";
    }

    @PostMapping("/personal_form")
    public @ResponseBody String postPersonalForm(@RequestBody PostFormDto postFormDto) {
        try {
            String errors = validatePersonalForm(postFormDto);
            if (errors.isEmpty()) {
                DocRequest docRequest = requestService.addPersonalRequest(postFormDto, RequestTypes.PERSONAL.getValue());
                emailService.sendSimpleMessage(docRequest.getOrganization().getEmail(), "Работающая Бурятия", "Ваша заявка получена.");
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
            if (postFormDto.getPerson() == null) {
                errors += "Заполните ФИО";
            } else {
                if (postFormDto.getPerson().getLastname() == null || postFormDto.getPerson().getLastname().isEmpty()) {
                    errors += "Заполните Фамилию";
                }
                if (postFormDto.getPerson().getFirstname() == null || postFormDto.getPerson().getFirstname().isEmpty()) {
                    errors += "Заполните Имя";
                }
            }
            if (postFormDto.getIsAgree() == false) {
                errors += "Необходимо подтвердить согласие на обработку персональных данных\n";
            }
            if (postFormDto.getIsProtect() == false) {
                errors += "Необходимо подтвердить обязательное выполнение предписания Управления Роспотребнадзора по Республике Бурятия\n";
            }
            if (postFormDto.getAddressFact() == null || postFormDto.getAddressFact().isEmpty()) {
                errors += "Необходимо заполнить список адресов\n";
            }
            if (postFormDto.getOrganizationInn() == null || postFormDto.getOrganizationInn().isEmpty()) {
                errors += "Заполните ИНН";
            } else if (postFormDto.getOrganizationInn().length() > 12) {
                errors += "Превышена длина ИНН";
            }
            if (postFormDto.getOrganizationPhone() == null || postFormDto.getOrganizationPhone().length() > 100) {
                errors += "Превышена длина номера телефона";
            }
            if (postFormDto.getOrganizationEmail() == null || postFormDto.getOrganizationEmail().length() > 100) {
                errors += "Превышена длина электронной почты";
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
}
