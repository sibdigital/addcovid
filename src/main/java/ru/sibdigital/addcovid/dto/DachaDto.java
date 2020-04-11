package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DachaDto {
    private Integer id;
    private String lastname;
    private String firstname;
    private String patronymic;
    private Integer age;
    private Boolean isAgree;
    private Boolean isProtect;
    private String email;
    private String phone;

    List<DachaAddrDto> addrList;
}
