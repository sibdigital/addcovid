package ru.sibdigital.addcovid.dto.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.subs.RegVerificationSignatureFile;
import ru.sibdigital.addcovid.model.subs.TpRequestSubsidyFile;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TpRequestSubsidyFileDto {
    private TpRequestSubsidyFile docFile;
    private TpRequestSubsidyFile signatureFile;
    private RegVerificationSignatureFile verificationSignatureFile;
}
