package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsDepartment;
import ru.sibdigital.addcovid.model.ClsDepartmentContact;

import java.util.List;


@Repository
public interface ClsDepartmentContactRepo extends JpaRepository<ClsDepartmentContact, Long> {
    List<ClsDepartmentContact> findAllByDepartmentAndType(ClsDepartment department, Integer type);
}
