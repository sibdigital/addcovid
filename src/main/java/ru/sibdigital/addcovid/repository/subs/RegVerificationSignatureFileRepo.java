package ru.sibdigital.addcovid.repository.subs;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sibdigital.addcovid.model.subs.RegVerificationSignatureFile;

public interface RegVerificationSignatureFileRepo extends JpaRepository<RegVerificationSignatureFile, Long> {
}