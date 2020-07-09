package ru.sibdigital.addcovid.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsPrincipal;

@Repository
public interface ClsPrincipalRepo extends CrudRepository<ClsPrincipal, Integer> {

}
