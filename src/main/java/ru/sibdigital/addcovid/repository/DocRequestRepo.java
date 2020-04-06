package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.DocRequest;

@Repository
public interface DocRequestRepo extends JpaRepository<DocRequest, Long> {
}
