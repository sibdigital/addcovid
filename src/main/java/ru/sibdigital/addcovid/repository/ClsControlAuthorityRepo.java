package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsControlAuthority;

@Repository
public interface ClsControlAuthorityRepo extends JpaRepository<ClsControlAuthority, Long>{

}
