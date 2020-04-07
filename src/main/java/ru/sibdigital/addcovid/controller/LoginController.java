package ru.sibdigital.addcovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.dto.PersonDto;
import ru.sibdigital.addcovid.model.DepUser;
import ru.sibdigital.addcovid.model.DocRequest;
import ru.sibdigital.addcovid.repository.DepUserRepo;
import ru.sibdigital.addcovid.repository.DocRequestRepo;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private DepUserRepo depUserRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/requests")
    public String requests(Map<String, Object> model, HttpSession session) {
        //model.put();
        DepUser depUser = (DepUser) session.getAttribute("user");
        //model.put("user", depUser);
        model.put("id_department", depUser.getIdDepartment().getId());
        model.put("department_name", depUser.getIdDepartment().getName());
        model.put("user_lastname", depUser.getLastname());
        model.put("user_firstname", depUser.getFirstname());
        return "requests";
    }

    @PostMapping("/authenticate")
    //public String login(Model model, String error, String logout) {
    public String authenticate(@ModelAttribute("log_form") DepUser inputDepUser, Map<String, Object> model, HttpSession session) {
        log.debug("вошли в LoginController.");

        DepUser depUser = depUserRepo.findByLogin(inputDepUser.getLogin().toLowerCase());

        if ((depUser == null) || (!depUser.getPassword().equals(inputDepUser.getPassword()) ) ){
            //не прошли аутентификацию
            log.debug("LoginController. Аутентификация не пройдена.");

            model.put("message", "Аутентификация не пройдена.");
            return "login";
        }
        log.debug("LoginController. Аутентификация пройдена.");

        session.setAttribute("user", depUser);

        return "redirect:/requests";
    }
}