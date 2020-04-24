package ru.sibdigital.addcovid.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsTypeRequest;

@Repository
public interface ClsTypeRequestRepo extends CrudRepository<ClsTypeRequest, Long> {

}
