package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.DocAddressFact;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocAddressFactRepo extends JpaRepository<DocAddressFact, Long> {

    @Query(nativeQuery = true, value = "select * from doc_address_fact where id_request = :id_request")
    Optional<List<DocAddressFact>> findByDocRequest(Long id_request);

}
