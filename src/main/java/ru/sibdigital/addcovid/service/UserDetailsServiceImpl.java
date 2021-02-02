package ru.sibdigital.addcovid.service;

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
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        ClsOrganization organization = null;
        if (login.matches("^([0-9]{10}|[0-9]{12})$")) {
            List<Integer> typeOrganizations = Arrays.asList(OrganizationTypes.JURIDICAL.getValue(),
                    OrganizationTypes.IP.getValue(), OrganizationTypes.SELF_EMPLOYED.getValue());
            organization = clsOrganizationRepo.findByInnAndPrincipalIsNotNull(login, typeOrganizations);
        } else {
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
            } else {
                throw new UsernameNotFoundException("User no found.");
            }
        } else {
            throw new UsernameNotFoundException("Organization no found.");
        }

        return builder.build();
    }
}
