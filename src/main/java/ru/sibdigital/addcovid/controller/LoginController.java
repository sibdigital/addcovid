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
        model.put("id_department", 1);
        if (depUser != null) {
            model.put("id_department", 1);
        }
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

//        if (error != null)
//            model.addAttribute("error", "Your username and password is invalid.");

//        if (logout != null)
//            model.addAttribute("message", "You have been logged out successfully.");


        //model.put("id_department", depUser.getIdDepartment());
        log.debug("LoginController. Вышли в LoginController.");

        session.setAttribute("user", depUser);

        return "redirect:/requests";
    }
}