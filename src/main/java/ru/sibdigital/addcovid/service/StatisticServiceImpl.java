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
    public Map<String, Object> getTotalStatistic(){
        Map<String, Object> statistic = new HashMap(5);

        Map<String, Object> peopleStatistic = new HashMap<>(3);
        peopleStatistic.put("accepted",docPersonRepo.getTotalApprovedPeopleByReviewStatus(1));
        peopleStatistic.put("declined",docPersonRepo.getTotalApprovedPeopleByReviewStatus(2));
        peopleStatistic.put("awaiting",docPersonRepo.getTotalApprovedPeopleByReviewStatus(0));


        statistic.put("peopleStatistic", peopleStatistic);
        statistic.put("forEachDayStatistic", docRequestRepo.getStatisticForEachDay());
        return statistic;
    }

    @Override
    public List<Map<String, Object>> getDepartmentRequestStatistic(){
        List<Map<String, Object>> rawStatistic = docRequestRepo.getRequestStatisticForEeachDepartment();
        /*statistic.put("totalPeople", docPersonRepo.getTotalPeople());
        statistic.put("totalApprovedPeople", docPersonRepo.getTotalApprovedPeopleByReviewStatus());*/
        return rawStatistic;
    }





}
