package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.addcovid.model.ClsFileType;

public interface ClsFileTypeRepo extends JpaRepository<ClsFileType, Long> {
}
