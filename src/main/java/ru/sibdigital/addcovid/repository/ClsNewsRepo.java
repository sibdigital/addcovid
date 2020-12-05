package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsNews;

import java.util.Optional;

@Repository
public interface ClsNewsRepo extends JpaRepository<ClsNews, Long> {
    Optional<ClsNews> findById(Long id);
}