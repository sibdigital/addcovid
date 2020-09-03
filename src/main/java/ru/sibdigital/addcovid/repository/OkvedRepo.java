package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.Okved;

@Repository
public interface OkvedRepo extends JpaRepository<Okved, Long> {

    Okved findByKindCode(String кодОКВЭД);
}
