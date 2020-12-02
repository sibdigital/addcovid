package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationContactDto {

    private Long id;
    private Long organizationId;
    private Integer type;
    private String contactPerson;
    private String contactValue;

}