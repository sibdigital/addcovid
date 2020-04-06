package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.DocAddressFact;

@Repository
public interface DocAddressFactRepo extends JpaRepository<DocAddressFact, Long> {
}
