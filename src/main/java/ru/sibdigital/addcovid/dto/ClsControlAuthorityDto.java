package ru.sibdigital.addcovid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ClsControlAuthorityDto {

    private Long id;
    private Long idParent;
    private String name;
    private String shortName;
    private Integer weight;
    private Boolean deleted;
}
