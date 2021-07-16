package ru.sibdigital.addcovid.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.sibdigital.addcovid.model.*;
import ru.sibdigital.addcovid.repository.ClsActionTypeRepo;
import ru.sibdigital.addcovid.repository.ClsOrganizationRepo;
import ru.sibdigital.addcovid.repository.RegActionHistoryRepo;

import javax.persistence.NonUniqueResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final static Logger authLog = LoggerFactory.getLogger("AuthLogger");

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ClsOrganizationRepo clsOrganizationRepo;

    @Autowired
    RegActionHistoryRepo regActionHistoryRepo;

    @Autowired
    ClsActionTypeRepo clsActionTypeRepo;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        ClsOrganization organization = null;
        User.UserBuilder builder = null;
        boolean isInnEntered = true;
        try {
        isInnEntered = login.matches("^([0-9]{10}|[0-9]{12})$");//true;
        organization = findOrganization(login);

        } catch (NonUniqueResultException | IncorrectResultSizeDataAccessException | InternalAuthenticationServiceException ex){
            authLog.info("Too many organizations found found for login " + login + " is inn entered " + isInnEntered);
            authLog.error(ex.getMessage(), ex);
            throw new UsernameNotFoundException("Too many organizations found.");
        }

        if (organization != null) {
            ClsPrincipal principal = organization.getPrincipal();
            addToHistory(organization, principal);
            if (principal != null) {
                builder = User.withUsername(organization.getId().toString());
//                builder.password(passwordEncoder.encode(principal.getPassword()));
                builder.password(principal.getPassword());
                builder.roles("USER");
                authLog.info(" User FOUND for login " + login + " is inn entered " + isInnEntered + " id org: " + organization.getId());
            } else {
                authLog.info(" User no found for login " + login + " is inn entered " + isInnEntered);
                throw new UsernameNotFoundException("User no found.");
            }
        } else {
            authLog.info("Organization no found. " + login + " is inn entered " + isInnEntered);
            throw new UsernameNotFoundException("Organization no found." );
        }

        return builder.build();
    }

    private ClsOrganization findOrganization(String login){
        ClsOrganization organization = null;
        if (login.matches("^([0-9]{10}|[0-9]{12})$")) {
            List<Integer> typeOrganizations = Arrays.asList(OrganizationTypes.JURIDICAL.getValue(),
                    OrganizationTypes.IP.getValue(), OrganizationTypes.SELF_EMPLOYED.getValue());
            organization = clsOrganizationRepo.findByInnAndPrincipalIsNotNull(login, typeOrganizations);
            if (organization == null) {
                typeOrganizations = Arrays.asList(OrganizationTypes.FILIATION.getValue(),
                        OrganizationTypes.REPRESENTATION.getValue(), OrganizationTypes.DETACHED.getValue(),
                        OrganizationTypes.KFH.getValue());
                organization = clsOrganizationRepo.findByInnAndPrincipalIsNotNull(login, typeOrganizations);
                if (organization != null) {
                    throw new UsernameNotFoundException("Need to enter email");
                }
            }
        } else {
            if (!login.contains(".")) {
                throw new UsernameNotFoundException("Need to enter inn or email");
            }
            List<Integer> typeOrganizations = Arrays.asList(OrganizationTypes.FILIATION.getValue(),
                    OrganizationTypes.REPRESENTATION.getValue(), OrganizationTypes.DETACHED.getValue(),
                    OrganizationTypes.KFH.getValue());
            organization = clsOrganizationRepo.findByEmailAndPrincipalIsNotNull(login, typeOrganizations);
            if (organization == null) {
                typeOrganizations = Arrays.asList(OrganizationTypes.JURIDICAL.getValue(),
                        OrganizationTypes.IP.getValue(), OrganizationTypes.SELF_EMPLOYED.getValue());
                organization = clsOrganizationRepo.findByEmailAndPrincipalIsNotNull(login, typeOrganizations);
                if (organization != null) {
                    throw new UsernameNotFoundException("Need to enter inn");
                }
            }
        }
        return organization;
    }

    private void addToHistory(ClsOrganization organization, ClsPrincipal principal){
        try {
            final ClsActionType actionType = clsActionTypeRepo.findByCode(ClsActionType.AUTH_CODE);
            RegActionHistory rah = RegActionHistory.builder()
                    .organization(organization)
                    .principal(principal)
                    .actionType(actionType)
                    .build();
            regActionHistoryRepo.save(rah);
        }catch (Exception ex){
            log.error(ex.getMessage(), ex);
        }
    }
}
