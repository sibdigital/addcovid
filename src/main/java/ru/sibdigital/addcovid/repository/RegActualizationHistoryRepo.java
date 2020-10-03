package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegActualizationHistory;

@Repository
public interface RegActualizationHistoryRepo extends JpaRepository<RegActualizationHistory, Long> {

}
