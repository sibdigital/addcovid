package ru.sibdigital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.model.DocAddressFact;

public interface DocAddressFactRepo extends JpaRepository<DocAddressFact, Long> {
}
