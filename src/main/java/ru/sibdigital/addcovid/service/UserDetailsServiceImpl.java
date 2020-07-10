package ru.sibdigital.addcovid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.model.ClsOrganization;
import ru.sibdigital.addcovid.model.ClsPrincipal;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Override
    public UserDetails loadUserByUsername(String inn) throws UsernameNotFoundException {
        ClsOrganization organization = clsOrganizationRepo.findByInnAndPrincipalIsNotNull(inn);

        User.UserBuilder builder = null;
        if (organization != null) {
            ClsPrincipal principal = organization.getPrincipal();
            if (principal != null) {
                builder = User.withUsername(inn);
                builder.password(principal.getPassword()); // TODO encoding password
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
