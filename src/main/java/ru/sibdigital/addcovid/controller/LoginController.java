package ru.sibdigital.addcovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.model.DepUser;
import ru.sibdigital.addcovid.repository.DepUserRepo;
import ru.sibdigital.addcovid.repository.DocRequestRepo;
import ru.sibdigital.addcovid.service.RequestService;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private DepUserRepo depUserRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ApplicationConstants applicationConstants;

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login(Model model) {

        model.addAttribute("application_name",applicationConstants.getApplicationName());
        return "login";
    }

    @GetMapping("/requests")
    public String requests(Map<String, Object> model, HttpSession session) {
        //model.put();
        DepUser depUser = (DepUser) session.getAttribute("user");
        if(depUser == null){
            return "404";
        }
        else {
            //model.put("user", depUser);
            model.put("id_department", depUser.getIdDepartment().getId());
            model.put("department_name", depUser.getIdDepartment().getName());
            model.put("user_lastname", depUser.getLastname());
            model.put("user_firstname", depUser.getFirstname());
            model.put("link_prefix", applicationConstants.getLinkPrefix());
            model.put("link_suffix", applicationConstants.getLinkSuffix());
            model.put("token", session.getAttribute("token"));
            model.put("application_name", applicationConstants.getApplicationName());
            model.put("contacts", applicationConstants.getContacts());
            return "requests";
        }
    }

}
