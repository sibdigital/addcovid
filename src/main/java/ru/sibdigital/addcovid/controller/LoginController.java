package ru.sibdigital.addcovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.sibdigital.addcovid.model.DepUser;
import ru.sibdigital.addcovid.repository.DepUserRepo;

@Controller
public class LoginController {

    @Autowired
    private DepUserRepo depUserRepo;

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/authenticate")
    //public String login(Model model, String error, String logout) {
    public String authenticate(@ModelAttribute("log_form") DepUser inputDepUser) {
        log.debug("вошли в LoginController.");

        DepUser depUser = depUserRepo.findByLogin(inputDepUser.getLogin().toLowerCase());

        if ((depUser == null) || (!depUser.getPassword().equals(inputDepUser.getPassword()) ) ){
            //не прошли аутентификацию
            log.debug("LoginController. Аутентификация не пройдена.");

            return "login";
        }
        log.debug("LoginController. Аутентификация  пройдена.");

//        if (error != null)
//            model.addAttribute("error", "Your username and password is invalid.");

//        if (logout != null)
//            model.addAttribute("message", "You have been logged out successfully.");

        log.debug("LoginController. Вышли в LoginController.");

        return "index";
    }
}