package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.DocEmployee;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocEmployeeRepo extends JpaRepository<DocEmployee, Long> {

    @Query(nativeQuery = true, value = "select * from doc_employee where id_organization = :id")
    Optional<List<DocEmployee>> findAllByOrganization(Long id);
}
