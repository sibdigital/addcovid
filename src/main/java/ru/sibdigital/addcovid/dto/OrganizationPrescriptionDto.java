package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.AdditionalAttributes;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationPrescriptionDto {

    private Long prescriptionId;
    private AdditionalAttributes additionalAttributes;
}
