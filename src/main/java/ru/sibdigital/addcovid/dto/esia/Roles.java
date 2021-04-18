package ru.sibdigital.addcovid.dto.esia;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Roles {

    @JsonAlias("elements")
    private List<UserOrganization> organizations;
}
