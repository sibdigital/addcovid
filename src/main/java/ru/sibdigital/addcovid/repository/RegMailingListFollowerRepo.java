package ru.sibdigital.addcovid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sibdigital.addcovid.model.ClsMailingList;
import ru.sibdigital.addcovid.model.ClsPrincipal;
import ru.sibdigital.addcovid.model.RegMailingListFollower;


@Repository
public interface RegMailingListFollowerRepo extends JpaRepository<RegMailingListFollower, Long> {

    RegMailingListFollower findByPrincipalAndMailingList(ClsPrincipal principal, ClsMailingList clsMailingList);


}