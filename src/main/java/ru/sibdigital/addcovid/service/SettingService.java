package ru.sibdigital.addcovid.service;

import ru.sibdigital.addcovid.model.ClsSettings;

import java.util.List;

public interface SettingService {

    ClsSettings findActual();

    ClsSettings findActualByKey(String key);

    String findActualByKey(String key, String defaultValue);

    List<ClsSettings> findAllActual();
}
