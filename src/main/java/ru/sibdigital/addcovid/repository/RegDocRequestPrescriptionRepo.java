package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegDocRequestPrescription;

@Repository
public interface RegDocRequestPrescriptionRepo extends JpaRepository<RegDocRequestPrescription, Long> {

}
