package ru.sibdigital.addcovid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sibdigital.addcovid.dto.PrincipalDto;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.ClsPrincipal;
import ru.sibdigital.addcovid.model.DocRequest;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.ClsPrincipalRepo;
import ru.sibdigital.addcovid.repository.DocRequestRepo;
import ru.sibdigital.addcovid.service.RequestService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CabinetController {

    @Autowired
    private ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    private DocRequestRepo docRequestRepo;

    @Autowired
    private ClsPrincipalRepo clsPrincipalRepo;

    @Autowired
    private RequestService requestService;

    @GetMapping("/cabinet")
    public String cabinet(HttpSession session, Model model) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return "404";
        } else {
            model.addAttribute("id_organization", id);
            model.addAttribute("token", session.getAttribute("token"));
            return "cabinet/main";
        }
    }

    @GetMapping("/organization")
    public @ResponseBody ClsOrganization getOrganization(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        return organization;
    }

    @GetMapping("/org_requests")
    public @ResponseBody List<DocRequest> getRequests(HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return null;
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        List<DocRequest> requests = docRequestRepo.getAllByInn(organization.getInn()).orElse(null);
        return requests;
    }

    @PostMapping("/save_pass")
    public @ResponseBody String savePassword(@RequestBody PrincipalDto principalDto, HttpSession session) {
        Long id = (Long) session.getAttribute("id_organization");
        if (id == null) {
            return "Пароль не обновлен";
        }
        ClsOrganization organization = clsOrganizationRepo.findById(id).orElse(null);
        if (organization != null && organization.getPrincipal() != null) {
            ClsPrincipal principal = organization.getPrincipal();
            principal.setPassword(principalDto.getPassword());
            clsPrincipalRepo.save(principal);
            return "Пароль обновлен";
        }
        return "Пароль не обновлен";
    }

}
