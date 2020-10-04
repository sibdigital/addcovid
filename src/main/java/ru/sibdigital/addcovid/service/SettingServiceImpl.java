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
}
