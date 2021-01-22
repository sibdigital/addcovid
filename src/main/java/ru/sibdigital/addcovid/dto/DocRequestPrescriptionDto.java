package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.RegDocRequestPrescriptionAttributes;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DocRequestPrescriptionDto {

    private Long prescriptionId;
    private RegDocRequestPrescriptionAttributes additionalAttributes;
}
