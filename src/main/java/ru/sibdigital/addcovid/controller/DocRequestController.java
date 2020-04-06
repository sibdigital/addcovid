package ru.sibdigital.addcovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.model.DocRequest;
import ru.sibdigital.addcovid.repository.DepUserRepo;
import ru.sibdigital.addcovid.repository.DocRequestRepo;

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

        //Optional<List<DocRequest>> docRequests =  docRequestRepo.getAllByDepartmentId(3);

        return null;//docRequests;
    }

}