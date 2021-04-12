package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RegOrganizationInspectionFileDto {

        private Long id;
        private Boolean isDeleted;
        private Timestamp timeCreate;
        private String attachmentPath;
        private String fileName;
        private String originalFileName;
        private String fileExtension;
        private String hash;
        private Long fileSize;

}
