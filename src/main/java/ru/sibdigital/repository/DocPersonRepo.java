package ru.sibdigital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.model.DocPerson;

public interface DocPersonRepo extends JpaRepository<DocPerson, Long> {
}
