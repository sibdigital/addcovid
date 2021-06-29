package ru.sibdigital.addcovid.config.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;

@Component
@Slf4j
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    @Override
    public void onApplicationEvent(final AuthenticationSuccessEvent e) {
        final Object source = e.getSource();

        if (source != null && source instanceof UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) source;
            final Object principal = upat.getPrincipal();
            //final Object credentials = upat.getCredentials();
            final Object details = upat.getDetails();
            String sdet = " ";
            if (principal != null && principal instanceof User){
                User up = (User) principal;
                sdet += "org id " + up.getUsername() + " success ";
            }
            if (details != null && details instanceof WebAuthenticationDetails){
                WebAuthenticationDetails wad = (WebAuthenticationDetails) details;
                final String remoteAddress = wad.getRemoteAddress();
                sdet += remoteAddress;
            }

            log.warn(sdet);
        }else{
            log.warn(" User login success ");
        }
    }
}
