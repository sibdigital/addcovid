package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegMailingHistory;

@Repository
public interface RegMailingHistoryRepo extends JpaRepository<RegMailingHistory, Long> {

}
