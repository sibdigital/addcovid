package ru.sibdigital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.model.ClsOrganization;

public interface ClsOrganizationRepo extends JpaRepository<ClsOrganization, Long> {
}
