package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class InspectionFileDto {

        private Long id;
        private Long idInspection;
        private Boolean isDeleted;
        private Timestamp timeCreate;
        private String attachmentPath;
        private String fileName;
        private String originalFileName;
        private String fileExtension;
        private String hash;
        private Long fileSize;

}
