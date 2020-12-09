package ru.sibdigital.addcovid.repository.classifier.gov;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.classifier.gov.RegEgrul;

import java.util.List;

@Repository
public interface RegEgrulRepo extends JpaRepository<RegEgrul, Long> {

    RegEgrul findByInn(String inn);
    @Query("select r from RegEgrul r where inn in :inns")
    List<RegEgrul> findAllByInnList(@Param("inns") List<String> inn);
}
