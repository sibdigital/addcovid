package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDto {

    private String organizationName;
    private String organizationShortName;
    private String organizationInn;
    private String organizationOgrn;
    private String organizationOkvedAdd;
    private String organizationOkved;
    private String organizationAddressJur;
    private String organizationEmail;
    private String organizationPhone;
    private Integer organizationType;
    private Boolean isSelfEmployed;

    private String password;

    private String egrulOkved;
    private String[] egrulOkvedAdd;

}
