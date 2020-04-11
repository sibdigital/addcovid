package ru.sibdigital.addcovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.repository.*;
import ru.sibdigital.addcovid.service.EmailService;
import ru.sibdigital.addcovid.service.RequestService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
public class DocRequestController {

    @Autowired
    private DepUserRepo depUserRepo;

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

    private static final Logger log = LoggerFactory.getLogger(DocRequestController.class);

    @GetMapping("/doc_requests")
    public DocRequest requests(@RequestParam String inn,@RequestParam String ogrn, Map<String, Object> model) {
        //if(inn!=null & !inn.isBlank()){
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
                                                     @PathVariable("status") Integer status) {

            Optional<List<DocRequestPrs>> docRequests = docRequestPrsRepo.findFirst100ByDepartmentAndStatusReviewOrderByTimeCreate(department, status);
            return docRequests;
    }

  //  @GetMapping("/list_requestByInnAndName/{id_department}/{status}/{innOrName}")
    @GetMapping("/list_request/{id_department}/{status}/{innOrName}")
    public Optional<List<DocRequestPrs>> listRequestByInnAndName(@PathVariable("id_department") Long id_department,
                                                  @PathVariable("status") Integer status, @PathVariable("innOrName") String innOrName) {

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
            emailService.sendSimpleMessage(docRequest.getOrganization().getEmail(), "Работающая Бурятия", text);
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
    public Optional<List<DocPerson>> getListPerson(@PathVariable("id_request") Long id_request){
        return docPersonRepo.findByDocRequest(id_request);
    }

    @GetMapping("/doc_address_fact/{id_request}")
    public Optional<List<DocAddressFact>> getListAddress(@PathVariable("id_request") Long id_request){
        return docAddressFactRepo.findByDocRequest(id_request);
    }
}