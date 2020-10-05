package ru.sibdigital.addcovid.service;

import ru.sibdigital.addcovid.model.ClsSettings;

public interface SettingService {

    ClsSettings findActual();

    ClsSettings findActualByKey(String key);

    String findActualByKey(String key, String defaultValue);
}
