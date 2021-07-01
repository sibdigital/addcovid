package ru.sibdigital.addcovid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.service.SettingService;
import ru.sibdigital.addcovid.utils.ConstantNames;

import org.springframework.security.core.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    private SettingService settingService;

    @GetMapping("/login")
    public String login(HttpSession session, HttpServletRequest request, Model model,
                        @RequestParam(value = "error", required = false) Boolean error) {
        model.addAttribute("application_name",applicationConstants.getApplicationName());

        //HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            org.springframework.security.authentication.BadCredentialsException e = null;
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        model.addAttribute("errorMessage", errorMessage);

        return "login";
    }

    @GetMapping("/check_esia")
    public @ResponseBody String checkEsia() {
        String esiaOn = settingService.findActualByKey(ConstantNames.SETTING_ESIA_ENABLE, "false");
        return esiaOn;
    }
}
