package ru.sibdigital.addcovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.repository.*;
import ru.sibdigital.addcovid.service.EmailService;
import ru.sibdigital.addcovid.service.RequestService;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.*;


@RestController
public class DocRequestController {

    @Autowired
    private ClsUserRepo clsUserRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    @Autowired
    private DocRequestPrsRepo docRequestPrsRepo;

    @Autowired
    private RequestService requestService;

    @Autowired
    private DocPersonRepo docPersonRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private DocAddressFactRepo docAddressFactRepo;

    @Autowired
    private ClsDepartmentRepo clsDepartmentRepo;

    @Autowired
    private ClsDistrictRepo clsDistrictRepo;


    @Autowired
    private ApplicationConstants applicationConstants;

    private static final Logger log = LoggerFactory.getLogger(DocRequestController.class);

    @GetMapping("/doc_requests")
    public DocRequest requests(@RequestParam String inn,@RequestParam String ogrn, Map<String, Object> model, HttpSession session) {
        //if(inn!=null & !inn.isBlank()){
        ClsUser clsUser = (ClsUser) session.getAttribute("user");
        if(clsUser == null){
            return null;
        }

        if(inn!=null & !inn.isEmpty()){
            return requestService.getLasRequestInfoByInn(inn);
        }

        if( ogrn!=null & !ogrn.isEmpty()){
            return requestService.getLastRequestInfoByOgrn(ogrn);
        }

        return null;
    }

    @GetMapping("/list_request/{id_department}/{status}")
    public Optional<List<DocRequestPrs>> listRequest(@PathVariable("id_department") ClsDepartment department,
                                                     //public List<DocRequest> listRequest(@PathVariable("id_department") ClsDepartment department,
                                                     @PathVariable("status") Integer status, HttpSession session) {

        ClsUser clsUser = (ClsUser) session.getAttribute("user");
        if(clsUser == null){
            return null;
        }
        Optional<List<DocRequestPrs>> docRequests = docRequestPrsRepo.findFirst100ByDepartmentAndStatusReviewOrderByTimeCreate(department, status);
        return docRequests;
    }

  //  @GetMapping("/list_requestByInnAndName/{id_department}/{status}/{innOrName}")
    @GetMapping("/list_request/{id_department}/{status}/{innOrName}")
    public Optional<List<DocRequestPrs>> listRequestByInnAndName(@PathVariable("id_department") Long id_department,
                                                  @PathVariable("status") Integer status, @PathVariable("innOrName") String innOrName, HttpSession session) {
        ClsUser clsUser = (ClsUser) session.getAttribute("user");
        if(clsUser == null){
            return null;
        }
        Optional<List<DocRequestPrs>> docRequests =  docRequestPrsRepo.getFirst100RequestByDepartmentIdAndStatusAndInnOrName(id_department, status, innOrName);
        return docRequests;
    }

    @PutMapping("/doc_requests/{id}")
    public DocRequest updateItem(@PathVariable("id") DocRequest docRequest,
                                 @RequestBody DocRequest obj,
                                 @RequestHeader("Authorization") String token){

        if(!requestService.isTokenValid(Integer.valueOf(token.replace("Bearer", "").trim()))) {
            return null;
        }

        Boolean changeFlag = false;

        if(docRequest.getStatusReview() != obj.getStatusReview()){
            String text = "";

            docRequest.setStatusReview(obj.getStatusReview());
            docRequest.setTimeReview(new Timestamp(System.currentTimeMillis()));

            changeFlag = true;

            if (docRequest.getStatusReview() == 1) {
                boolean massAcсept = true;
                if (massAcсept == true){
                    final Optional<List<DocRequest>> lastRequestByInn =
                            docRequestRepo.getLastRequestByInnAndStatus(docRequest.getOrganization().getInn(), 0);
                    if (lastRequestByInn.isPresent()){
                        final List<DocRequest> docRequests = lastRequestByInn.get();
                        for (DocRequest dr: docRequests){
                            dr.setStatusReview(docRequest.getStatusReview());
                        }
                        docRequestRepo.saveAll(docRequests);
                    }
                }
                text = "Ваше заявление рассмотрено и одобрено.";
            }
            else if (docRequest.getStatusReview() == 2) {
                text = "Ваша заявка отклонена по причине: " + docRequest.getRejectComment();
            }
            emailService.sendSimpleMessage(docRequest.getOrganization().getEmail(), applicationConstants.getApplicationName(), text);
        }

        Long oldDepartmentId = docRequest.getDepartment().getId();
        if(oldDepartmentId != obj.getDepartment().getId()){
            ClsDepartment clsDepartment = clsDepartmentRepo.getOne(obj.getDepartment().getId());
            docRequest.setDepartment(clsDepartment);
            docRequest.setOld_department_id(oldDepartmentId);
            changeFlag = true;
        }

        return changeFlag ? docRequestRepo.save(docRequest) : null;
    }

    @GetMapping("/doc_persons/{id_request}")
    public Map<String, Object> listPerson(@PathVariable("id_request") Long id_request,
                                                   @RequestParam(value = "start", required = false) Integer start,
                                                   @RequestParam(value = "count", required = false) Integer count,
                                                   HttpSession session){
        int page = start == null ? 0 : start / 50;
        int size = count == null ? 50 : count;

        Page<DocPerson> docPersons = docPersonRepo.findAllByDocRequest(id_request, PageRequest.of(page, size));

        Map<String, Object> result = new HashMap<>();
        result.put("data", docPersons.getContent());
        result.put("pos", (long) page * size);
        result.put("total_count", docPersons.getTotalElements());
        return result;
    }

    @GetMapping("/doc_address_fact/{id_request}")
    public Optional<List<DocAddressFact>> getListAddress(@PathVariable("id_request") Long id_request, HttpSession session){
        return docAddressFactRepo.findByDocRequest(id_request);
    }

    @GetMapping("/doc_requests/{id_request}")
    public Optional<DocRequest> getDocRequest(@PathVariable("id_request") Long id_request) {
        return docRequestRepo.findById(id_request);
    }

    @GetMapping("/cls_departments")
    public List<ClsDepartment> getListDepartments(HttpSession session) {
        List<ClsDepartment> list =  clsDepartmentRepo.findByIsDeletedFalseOrderByIdAsc();
        try {
            List<ClsDepartment> presult = new ArrayList<>();
            ClsDepartment first = clsDepartmentRepo.findById(4L).get();
            ClsDepartment last = clsDepartmentRepo.findById(1L).get();
            for (ClsDepartment department : list) {
                if (department.getId() != 1L && department.getId() != 4L) {
                    presult.add(department);
                }
            }
            List<ClsDepartment> result = new ArrayList<>();
            result.add(first);
            result.addAll(presult);
            result.add(last);
            return result;
        }catch (Exception ex){

        }
        return list;
    }

    @GetMapping("/cls_districts")
    public List<ClsDistrict> getListDistricts() {
        List<ClsDistrict> list =  clsDistrictRepo.findAll(Sort.by("id"));
        return list;
    }

    @GetMapping("/actualized_doc_requests")
    public List<DocRequestPrs> getActualizedRequests(@RequestParam(name = "inn") String inn) {
        String innParam = inn != null ? inn.trim() : inn;
        List<DocRequestPrs> list  = docRequestPrsRepo.getActualizedRequests(innParam);
        return list;
    }
}
