package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sibdigital.addcovid.model.DocDachaPerson;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DachaDto {
    private Integer id;
    private String district;
    private String address;
    private LocalDate validDate;
    private String link;
    private String raion;
    private String naspunkt;
    private Boolean isAgree;
    private Boolean isProtect;
    private Timestamp timeCreate;
    private Integer statusImport;
    private Timestamp timeImport;
    private Integer statusReview;
    private Timestamp timeReview;
    private String rejectComment;
    private String phone;
    private String email;

    List<DachaPersonDto> personList;
}
