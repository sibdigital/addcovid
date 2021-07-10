package ru.sibdigital.addcovid.dto.subs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DocRequestSubsidyPostDto {
    private Long id;
    private Long organizationId;
    private Long subsidyId;
    private String subsidyRequestStatusCode;
    private String reqBasis;
}
