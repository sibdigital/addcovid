package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsNews;
import ru.sibdigital.addcovid.model.RegNewsFile;

import java.util.List;

@Repository
public interface RegNewsFileRepo extends JpaRepository<RegNewsFile, Long> {

    List<RegNewsFile> findAllByNews(ClsNews news);
}
