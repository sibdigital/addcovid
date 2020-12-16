package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegDocRequestFile;

@Repository
public interface RegDocRequestFileRepo extends JpaRepository<RegDocRequestFile, Long> {

}
