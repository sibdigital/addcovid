package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DocRequestDto {

    private Long id;
    private String activityKind;
    private Integer statusReview;
    private String statusReviewName;
    private String departmentName;
    private Timestamp timeCreate;
    private Timestamp timeReview;
    private Long typeRequestId;
}
