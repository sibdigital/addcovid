package ru.sibdigital.addcovid.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.sibdigital.addcovid.service.StatisticService;

@Log4j2
@Controller
public class StatisticController {

    @Autowired
    StatisticService statisticService;

    @Autowired
    private ApplicationConstants applicationConstants;

    @GetMapping(value = "/statistic")
    public String getStatisticPage(Model model){

        model.addAttribute("totalStatistic", statisticService.getTotalStatistic());
        model.addAttribute("departmentStatistic", statisticService.getDepartmentRequestStatistic());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "statistic";
    }

    @GetMapping(value = "/dacha/statistic")
    public String getDachaStatisticPage(Model model){

        model.addAttribute("totalStatistic", statisticService.getTotalDachaStatistic());
        model.addAttribute("nearestDaysStatistic", statisticService.getNearestDaysDachaRequestStatistic());
        model.addAttribute("application_name", applicationConstants.getApplicationName());
        return "dacha_statistic";
    }






}
