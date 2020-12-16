package ru.sibdigital.addcovid.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.model.RegHelp;
import ru.sibdigital.addcovid.repository.RegHelpRepo;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller
public class HelpController {

    @Autowired
    private RegHelpRepo regHelpRepo;

    @Autowired
    private ApplicationConstants applicationConstants;

    @GetMapping("helps")
    public String helps(@RequestParam(value = "key", required = false) String key, Model model) {
        if (key != null) {
            RegHelp regHelp = regHelpRepo.findByKey(key).orElse(null);
            model.addAttribute("help_data", regHelp);
        }
        List<RegHelp> regHelps = regHelpRepo.findAll(Sort.by(Sort.Direction.ASC, "name"));
        model.addAttribute("help_list", regHelps);
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
        return "help";
    }

//    @GetMapping("help")
//    public String getHelpById(HttpSession session, Model model, @RequestParam(value = "id", required = false) Long id, @RequestParam(value = "key", required = false) String key) {
//        RegHelp regHelp = null;
//        System.out.println(key);
//
//        if (id != null) {
//            regHelp = regHelpRepo.findById(id).orElse(null);
//        }
//        else if (key != null) {
//            regHelp = regHelpRepo.findByKey(key).orElse(null);
//        }
//
//        model.addAttribute("help_data", regHelp);
//        model.addAttribute("application_name", applicationConstants.getApplicationName());
//        model.addAttribute("link_prefix", applicationConstants.getLinkPrefix());
//        model.addAttribute("link_suffix", applicationConstants.getLinkSuffix());
//        return "help";
//    }

    @GetMapping("helpsdata")
    public @ResponseBody List<RegHelp> helps(HttpSession session) {
        List<RegHelp> regHelps = regHelpRepo.findAll();
        return regHelps;
    }

    @GetMapping("help")
    public @ResponseBody RegHelp getHelpById(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "key", required = false) String key
    ) {
        RegHelp regHelp = null;
        System.out.println(key);

        if (id != null) {
            regHelp = regHelpRepo.findById(id).orElse(null);
        }
        else if (key != null) {
            regHelp = regHelpRepo.findByKey(key).orElse(null);
        }

        return regHelp;
    }

//    @GetMapping("help")
//    public @ResponseBody RegHelp getHelp(@RequestParam(value = "id") Long id) {
//        RegHelp results = regHelpRepo.findById(id).orElse(null);
//        return results;
//    }

}
