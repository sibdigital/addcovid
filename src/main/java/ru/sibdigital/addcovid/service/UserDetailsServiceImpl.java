package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.ClsPrincipal;
import ru.sibdigital.addcovid.model.OrganizationTypes;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        ClsOrganization organization = null;
        boolean isInnEntered = true;
        if (login.matches("^([0-9]{10}|[0-9]{12})$")) {
            List<Integer> typeOrganizations = Arrays.asList(OrganizationTypes.JURIDICAL.getValue(),
                    OrganizationTypes.IP.getValue(), OrganizationTypes.SELF_EMPLOYED.getValue());
            organization = clsOrganizationRepo.findByInnAndPrincipalIsNotNull(login, typeOrganizations);
        } else {
            isInnEntered = false;
            List<Integer> typeOrganizations = Arrays.asList(OrganizationTypes.FILIATION.getValue(),
                    OrganizationTypes.REPRESENTATION.getValue(), OrganizationTypes.DETACHED.getValue(),
                    OrganizationTypes.KFH.getValue());
            organization = clsOrganizationRepo.findByEmailAndPrincipalIsNotNull(login, typeOrganizations);
        }

        User.UserBuilder builder = null;
        if (organization != null) {
            ClsPrincipal principal = organization.getPrincipal();
            if (principal != null) {
                builder = User.withUsername(organization.getId().toString());
//                builder.password(passwordEncoder.encode(principal.getPassword()));
                builder.password(principal.getPassword());
                builder.roles("USER");
                log.warn(" User FOUND for login " + login + " is inn entered " + isInnEntered + "id org: " + organization.getId());
            } else {
                log.warn(" User no found for login " + login + " is inn entered " + isInnEntered);
                throw new UsernameNotFoundException("User no found.");
            }
        } else {
            log.warn("Organization no found. " + login + " is inn entered " + isInnEntered);
            throw new UsernameNotFoundException("Organization no found." );
        }

        return builder.build();
    }
}
