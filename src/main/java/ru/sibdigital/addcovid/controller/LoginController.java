package ru.sibdigital.addcovid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.service.SettingService;
import ru.sibdigital.addcovid.utils.ConstantNames;

@Controller
public class LoginController {

    @Autowired
    private ApplicationConstants applicationConstants;

    @Autowired
    private SettingService settingService;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("application_name",applicationConstants.getApplicationName());
        return "login";
    }

    @GetMapping("/check_esia")
    public @ResponseBody String checkEsia() {
        String esiaOn = settingService.findActualByKey(ConstantNames.SETTING_ESIA_ENABLE, "false");
        return esiaOn;
    }
}
