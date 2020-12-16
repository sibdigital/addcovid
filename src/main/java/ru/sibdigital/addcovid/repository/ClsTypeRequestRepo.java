package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsTypeRequest;

import java.util.List;

@Repository
public interface ClsTypeRequestRepo extends CrudRepository<ClsTypeRequest, Long> {
    public List<ClsTypeRequest> findAllByOrderBySortWeight();
}
