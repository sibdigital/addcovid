package ru.sibdigital.addcovid.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.sibdigital.addcovid.dto.PrincipalDto;
import ru.sibdigital.addcovid.model.ClsOrganization;
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

    @Value("${link.prefix:http://fs.govrb.ru}")
    private String linkPrefix;

    @Value("${link.suffix:}")
    private String linkSuffix;

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String login(HttpSession session) {
        session.removeAttribute("id_organization");
        return "redirect:/login";
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
            model.put("link_prefix", linkPrefix);
            model.put("link_suffix", linkSuffix);
            model.put("token", session.getAttribute("token"));
            return "requests";
        }
    }

    @GetMapping("/authenticate")
    public String authenticateGet(){
        return "404";
    }

    @PostMapping("/authenticate")
    public String authenticate(@ModelAttribute("log_form") PrincipalDto principal, Map<String, Object> model, HttpSession session) {

        if (principal == null) {
            return "login";
        }

        ClsOrganization organization = requestService.findOrganizationByInn(principal.getLogin());

        if ((organization == null) || (!organization.getPrincipal().getPassword().equals(principal.getPassword()))) {
            //не прошли аутентификацию
            log.debug("LoginController. Аутентификация не пройдена.");

            model.put("message", "Аутентификация не пройдена.");
            return "login";
        }
        log.debug("LoginController. Аутентификация пройдена.");

        session.setAttribute("id_organization", organization.getId());
        session.setAttribute("token", organization.hashCode());

        return "redirect:/cabinet";
    }
}