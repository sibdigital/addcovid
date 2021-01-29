package ru.sibdigital.addcovid.repository.classifier.gov;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrip;

import java.util.List;

@Repository
public interface RegEgripRepo extends JpaRepository<RegEgrip, Long> {

    List<RegEgrip> findByInnAndActiveStatus(String inn, int status);

    @Query("select r from RegEgrip r where inn in :inns")
    List<RegEgrip> findAllByInnList(@Param("inns") List<String> inn);
}
