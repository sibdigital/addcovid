package ru.sibdigital.addcovid.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sibdigital.addcovid.model.DocDachaPerson;

public interface DocDachaPersonRepo extends CrudRepository<DocDachaPerson, Long> {
}
