package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsDepartment;

@Repository
public interface ClsDepartmentRepo extends JpaRepository<ClsDepartment, Long> {
}
