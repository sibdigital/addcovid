package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class AdditionalFields {

    private UUID[] okvedIds;
    private Long[] organizationIds;
}
