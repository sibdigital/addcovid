package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {

    private Long id;
    private Long organizationId;
    private PersonDto person;
    private Boolean isVaccinatedFlu;
    private Boolean isVaccinatedCovid;
    private Boolean isDeleted;

}
