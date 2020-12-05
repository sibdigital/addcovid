package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsOrganizationContact;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClsOrganizationContactRepo extends JpaRepository<ClsOrganizationContact, Long>{
    @Query(nativeQuery = true, value = "select * from cls_organization_contact where id_organization = :id")
    Optional<List<ClsOrganizationContact>> findAllByOrganization(Long id);
}
