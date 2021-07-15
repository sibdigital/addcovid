package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsActionType;

@Repository
public interface ClsActionTypeRepo extends JpaRepository<ClsActionType, Long> {
    ClsActionType findByCode(String code);
}
