package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegActionHistory;


@Repository
public interface RegActionHistoryRepo extends JpaRepository<RegActionHistory, Long> {

}
