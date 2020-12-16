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
public class ClsPrescriptionDto {

    private Long id;
    private String name;
    private String typeRequestName;
    private Timestamp timePublication;
    private boolean isAccepted;
}
