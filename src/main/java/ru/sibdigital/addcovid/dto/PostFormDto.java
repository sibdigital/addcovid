package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PostFormDto {
    private Long organizationId;
    private long departmentId;

    private String organizationName;
    private String  organizationShortName;
    private String  organizationInn;
    private String  organizationOgrn;
    private String  organizationAddressJur;
    private String organizationOkvedAdd;
    private String  organizationOkved;
    private String  organizationEmail;
    private String  organizationPhone;

    private List<FactAddressDto> addressFact;

    private List<PersonDto> persons;

    private Long personOfficeCnt;
    private Long personRemoteCnt;
    private Long personSlrySaveCnt;
    private Long personOfficeFactCnt;

    //private MultipartFile attachment;
    private String attachment;
    private String attachmentFilename;

    private Boolean isAgree;
    private Boolean isProtect;
    private String reqBasis;
}
