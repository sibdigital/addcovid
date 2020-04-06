package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsOrganization;

@Repository
public interface ClsOrganizationRepo extends JpaRepository<ClsOrganization, Long> {

}
