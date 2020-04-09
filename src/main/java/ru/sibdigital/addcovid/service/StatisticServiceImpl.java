package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.repository.DocPersonRepo;
import ru.sibdigital.addcovid.repository.DocRequestRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    DocPersonRepo docPersonRepo;

    @Autowired
    DocRequestRepo docRequestRepo;

    @Override
    public Map getTotalStatistic(){
        Map statistic = new HashMap(5);
        statistic.put("totalPeople", docPersonRepo.getTotalPeople());
        statistic.put("totalApprovedPeople", docPersonRepo.getTotalApprovedPeople());
        statistic.put("forEachDayStatistic", docRequestRepo.getStatisticForEachDay());
        return statistic;
    }

    @Override
    public List<Map<String, Object>> getDepartmentRequestStatistic(){
        List<Map<String, Object>> rawStatistic = docRequestRepo.getRequestStatisticForEeachDepartment();
        /*statistic.put("totalPeople", docPersonRepo.getTotalPeople());
        statistic.put("totalApprovedPeople", docPersonRepo.getTotalApprovedPeople());*/
        return rawStatistic;
    }





}
