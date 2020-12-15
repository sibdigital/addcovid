package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsSettings;

import java.util.Optional;

@Repository
public interface ClsSettingsRepo extends JpaRepository<ClsSettings, Long> {

    @Query(value = "select s from ClsSettings s where s.status = 1")
    Optional<ClsSettings> getActual();

    @Query(value = "select s from ClsSettings s where s.key = :key and s.status = 1")
    Optional<ClsSettings> getActualByKey(String key);

    @Query(value = "select s from ClsSettings s where s.key = 'requestsStyles' ")
    Optional<ClsSettings> getRequestsStatusStyle();

    @Query(value = "select s from ClsSettings s where s.key = 'consentPersonalData' ")
    Optional<ClsSettings> getConsentPersonalData();
}
