package ru.sibdigital.addcovid.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import ru.sibdigital.addcovid.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity()
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver;

    @Autowired
    private OAuth2AccessTokenResponseClient accessTokenResponseClient;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/libs/**", "/css/**").permitAll()
                .antMatchers("/js/**").permitAll() // TODO удалить, когда доступ нужно будет закрыть
                .antMatchers("/", "/registration", "/activate", "/recovery").permitAll()
                .antMatchers("/form", "/barber", "/typed_form", "/personal_form", "/dacha", "/actualize_form", "/news_form", "/index_list").permitAll() // TODO удалить, когда доступ нужно будет закрыть
                .antMatchers("/cls_type_requests", "/cls_type_request/*", "/cls_departments", "/cls_districts", "/actualized_doc_requests", "/doc_requests/*", "/doc_persons/*", "/doc_address_fact/*" ).permitAll() // TODO удалить, когда доступ нужно будет закрыть
                .antMatchers("/upload", "/uploadpart", "/upload/protocol", "/download/*").permitAll() // TODO удалить, когда доступ нужно будет закрыть
                .antMatchers("/login", "/check_esia").permitAll()
                .antMatchers("/favicon.ico","/logo.png").permitAll()
                .antMatchers("/egrul", "/egrip").permitAll()
                .antMatchers("/news*", "/news/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/cabinet", true)
                .failureUrl("/login?error=true")
                .and()
                .oauth2Login(oauth2Login -> oauth2Login
                        .authorizationEndpoint(authorizationEndpoint ->
                                        authorizationEndpoint.authorizationRequestResolver(
                                                this.customAuthorizationRequestResolver
                                        )
                        )
                        .tokenEndpoint(tokenEndpoint ->
                                tokenEndpoint.accessTokenResponseClient(this.accessTokenResponseClient)
                        )
                        .loginPage("/login")
                        .defaultSuccessUrl("/cabinet")
                )
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .and()
                .sessionManagement()
                .invalidSessionUrl("/login");
    }
}
