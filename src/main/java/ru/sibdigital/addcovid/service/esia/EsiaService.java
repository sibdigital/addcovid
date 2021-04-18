package ru.sibdigital.addcovid.service.esia;

import ru.sibdigital.addcovid.dto.esia.Organization;
import ru.sibdigital.addcovid.dto.esia.Roles;
import ru.sibdigital.addcovid.dto.esia.Token;
import ru.sibdigital.addcovid.dto.esia.User;

import java.util.List;

public interface EsiaService {

    Token getAccessToken(List<String> scopes);

    User getUser();

    Roles getUserRoles();

    List<Organization> getUserOrganizations();

    Organization getOrganization(Token token, Long id);
}
