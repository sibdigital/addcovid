package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.DocRequestPrs;
import ru.sibdigital.addcovid.model.RegHelp;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegHelpRepo extends JpaRepository<RegHelp, Long> {

    @Query(value = "select * from reg_help where key = :key",
            nativeQuery = true)
    Optional<RegHelp> findByKey(@Param("key") String key);
}
