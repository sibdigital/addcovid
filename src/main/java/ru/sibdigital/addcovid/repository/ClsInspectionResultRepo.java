package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsInspectionResult;

@Repository
public interface ClsInspectionResultRepo extends JpaRepository<ClsInspectionResult, Long>{

}
