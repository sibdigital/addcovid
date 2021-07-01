package ru.sibdigital.addcovid.config.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {


    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        final Object source = e.getSource();
        final String message = e.getException() != null ? e.getException().getMessage() : " null message ";

        if (source != null && source instanceof UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) source;
            final Object principal = upat.getPrincipal();

            final Object details = upat.getDetails();
            String sdet = message + " ";
            if (details != null && details instanceof WebAuthenticationDetails){
                WebAuthenticationDetails wad = (WebAuthenticationDetails) details;
                final String remoteAddress = wad.getRemoteAddress();
                sdet += remoteAddress;
            }else{
                String sd = details != null ? details.toString() : " no detail";
                sdet += (" " + sd);
            }
            //final Object credentials = upat.getCredentials();
            log.warn(" Auth for " + principal + " fail " + sdet + " "
                    + (upat.getCredentials() != null ? ( "pwd: " + upat.getCredentials()) : "null pwd " ));
        }else{
            log.warn(" Auth unknown fail " + message);
        }

    }
}
