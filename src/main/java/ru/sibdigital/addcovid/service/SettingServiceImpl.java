package ru.sibdigital.addcovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.model.ClsSettings;
import ru.sibdigital.addcovid.repository.ClsSettingsRepo;

@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private ClsSettingsRepo clsSettingsRepo;

    public ClsSettings findActual() {
        return clsSettingsRepo.getActual().orElse(null);
    }

    public ClsSettings findActualByKey(String key) {
        return clsSettingsRepo.getActualByKey(key).orElse(null);
    }

    public String findActualByKey(String key, String defaultValue){
        ClsSettings settings = findActualByKey(key);
        String ret = settings != null ? settings.getStringValue() : defaultValue;
        return ret;
    }
}
