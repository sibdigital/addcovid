package ru.sibdigital.addcovid.dto.esia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Organization {

    private String shortName;
    private String fullName;
    private String type;
    private String ogrn;
    private String inn;
    private String kpp;

    private UserOrganization userOrganization;
}
