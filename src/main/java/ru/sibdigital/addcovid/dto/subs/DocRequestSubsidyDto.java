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
public class DocRequestSubsidyDto {
    private Long id;
    private String subsidyName;
    private Long subsidyId;
    private String departmentName;
    private String subsidyRequestStatusName;
    private String subsidyRequestStatusCode;
    private Timestamp timeCreate;
    private Timestamp timeUpdate;
    private Timestamp timeReview;
    private Timestamp timeSend;
    private String reqBasis;
    private String resolutionComment;
    private String districtName;
    private String statusActivityName;
    private Boolean isDeleted;
}
