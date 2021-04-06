package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.ClsControlAuthority;
import ru.sibdigital.addcovid.model.ClsInspectionResult;

import java.sql.Date;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RegOrganizationInspectionDto {
    private Long id;
    private Long organizationId;
    private Long controlAuthorityId;
    private Long inspectionResultId;
    private Date dateOfInspection;
    private String comment;
}
