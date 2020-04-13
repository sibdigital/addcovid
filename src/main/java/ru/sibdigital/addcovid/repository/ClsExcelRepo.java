package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsExcel;

@Repository
public interface ClsExcelRepo extends JpaRepository<ClsExcel, Long> {
}
