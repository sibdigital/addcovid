package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegOrganizationPrescription;

@Repository
public interface RegOrganizationPrescriptionRepo extends JpaRepository<RegOrganizationPrescription, Long> {

}
