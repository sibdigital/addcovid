package ru.sibdigital.addcovid.repository.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClsOrganizationSearchCriteria {

    private String inn;
    private Long idPrescription;
    private List<Integer> typeOrganizations;
    private String email;
    private Boolean isActivated;

    private Long egrulId;
    private Long egripId;
    private Long filialId;
}
