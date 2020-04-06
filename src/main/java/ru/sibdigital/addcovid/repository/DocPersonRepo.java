package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.DocPerson;

@Repository
public interface DocPersonRepo extends JpaRepository<DocPerson, Long> {
}
