package ru.sibdigital.addcovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.model.ClsUser;
import ru.sibdigital.addcovid.repository.ClsUserRepo;
import ru.sibdigital.addcovid.repository.DocRequestRepo;
import ru.sibdigital.addcovid.service.RequestService;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private ClsUserRepo clsUserRepo;

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
        ClsUser clsUser = (ClsUser) session.getAttribute("user");
        if(clsUser == null){
            return "404";
        }
        else {
            //model.put("user", clsUser);
            model.put("id_department", clsUser.getIdDepartment().getId());
            model.put("department_name", clsUser.getIdDepartment().getName());
            model.put("user_lastname", clsUser.getLastname());
            model.put("user_firstname", clsUser.getFirstname());
            model.put("link_prefix", applicationConstants.getLinkPrefix());
            model.put("link_suffix", applicationConstants.getLinkSuffix());
            model.put("token", session.getAttribute("token"));
            model.put("application_name", applicationConstants.getApplicationName());
            model.put("contacts", applicationConstants.getContacts());
            return "requests";
        }
    }

}
