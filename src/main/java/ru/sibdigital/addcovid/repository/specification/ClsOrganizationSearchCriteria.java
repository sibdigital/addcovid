package ru.sibdigital.addcovid.repository.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClsOrganizationSearchCriteria {

    private String inn;
    private Long idPrescription;
    private Integer typeOrganization;
    private String email;
    private Boolean isActivated;

    private Long egrulId;
    private Long egripId;
    private Long filialId;
}
