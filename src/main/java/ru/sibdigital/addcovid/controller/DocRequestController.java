package ru.sibdigital.addcovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.model.DocRequest;
import ru.sibdigital.addcovid.repository.DepUserRepo;
import ru.sibdigital.addcovid.repository.DocRequestRepo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
public class DocRequestController {

    @Autowired
    private DepUserRepo depUserRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    private static final Logger log = LoggerFactory.getLogger(DocRequestController.class);

    @GetMapping("/doc_requests")
    public Optional<List<DocRequest>> requests(Map<String, Object> model) {

        Optional<List<DocRequest>> docRequests =  docRequestRepo.getAllByDepartmentId(3l, 0);

        return docRequests;
    }

    @GetMapping("/list_request/{id_department}")
    public Optional<List<DocRequest>> listRequest(@PathVariable("id_department") Long id_department) {

        Optional<List<DocRequest>> docRequests =  docRequestRepo.getAllByDepartmentId(id_department, 0);

        return docRequests;
    }

    @PutMapping("/doc_requests/{id}")
    public DocRequest updateItem(@PathVariable("id") DocRequest docRequest, @RequestBody DocRequest obj){
        obj.setTimeReview(new Timestamp(System.currentTimeMillis()));
        BeanUtils.copyProperties(obj, docRequest, "id");

        return docRequestRepo.save(docRequest);
    }

}