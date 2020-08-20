package ru.sibdigital.addcovid.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsUser;

@Repository
public interface ClsUserRepo extends CrudRepository<ClsUser, Long> {

    ClsUser findByLogin(String login);

}
