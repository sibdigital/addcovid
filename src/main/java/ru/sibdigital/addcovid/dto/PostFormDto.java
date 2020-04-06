package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
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

    public String sha256() {
        try{
            String base = organizationName + organizationInn + organizationOgrn;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }


}
