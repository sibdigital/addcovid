package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.addcovid.model.subs.ClsSubsidyRequestStatus;

public interface ClsSubsidyRequestStatusRepo extends JpaRepository<ClsSubsidyRequestStatus, Long> {
}
