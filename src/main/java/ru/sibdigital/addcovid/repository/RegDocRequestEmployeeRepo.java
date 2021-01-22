package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegDocRequestEmployee;

@Repository
public interface RegDocRequestEmployeeRepo extends JpaRepository<RegDocRequestEmployee, Long> {

}
