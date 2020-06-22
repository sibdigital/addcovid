package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsDistrict;

@Repository
public interface ClsDistrictRepo extends JpaRepository<ClsDistrict, Long> {

}
