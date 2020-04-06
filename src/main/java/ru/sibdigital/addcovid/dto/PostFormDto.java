package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PostFormDto {

    private long departmentId;

    private String organizationName;
    private String  organizationShortName;
    private String  organizationInn;
    private String  organizationOgrn;
    private String  organizationAddressJur;
    private List<String> organizationOkvedAdd;
    private String  organizationOkved;
    private String  organizationEmail;
    private String  organizationPhone;

    private List<FactAddressDto> addressFact;

    private List<PersonDto> persons;

    private Long personOfficeCnt;
    private Long personRemoteCnt;
    private Long personSlrySaveCnt;
    private Long personOfficeFactCnt;

    private MultipartFile attachment;



}
