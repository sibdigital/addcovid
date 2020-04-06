package ru.sibdigital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.model.ClsDepartment;

public interface ClsDepartmentRepo extends JpaRepository<ClsDepartment, Long> {
}
