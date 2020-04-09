package ru.sibdigital.addcovid.service;

import java.util.List;
import java.util.Map;


public interface StatisticService {


    Map getTotalStatistic();

    List<Map<String, Object>> getDepartmentRequestStatistic();
}
