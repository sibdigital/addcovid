package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegOrganizationOkved;

@Repository
public interface RegOrganizationOkvedRepo extends JpaRepository<RegOrganizationOkved, Long> {

}
