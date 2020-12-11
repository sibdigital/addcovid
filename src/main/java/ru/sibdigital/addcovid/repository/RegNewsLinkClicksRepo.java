package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.RegNewsLinkClicks;


@Repository
public interface RegNewsLinkClicksRepo extends JpaRepository<RegNewsLinkClicks, Long> {
}
