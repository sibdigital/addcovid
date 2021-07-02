package ru.sibdigital.addcovid.config.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String error = "true";
        if (exception.getMessage().equals("Need to enter inn")) {
            error = "inn";
        } else if (exception.getMessage().equals("Need to enter email")) {
            error = "email";
        } else if (exception.getMessage().equals("Need to enter inn or email")) {
            error = "inn_or_email";
        }
        String redirectURL = "/login?error=" + error;

        super.setDefaultFailureUrl(redirectURL);
        super.onAuthenticationFailure(request, response, exception);
    }
}
