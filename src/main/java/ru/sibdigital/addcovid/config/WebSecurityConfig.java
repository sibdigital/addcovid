package ru.sibdigital.addcovid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.sibdigital.addcovid.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity()
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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
                .antMatchers("/", "/registration", "/recovery").permitAll()
                .antMatchers("/form", "/barber", "/typed_form", "/personal_form", "/dacha").permitAll() // TODO удалить, когда доступ нужно будет закрыть
                .antMatchers("/cls_type_requests", "/cls_type_request/*", "/cls_departments", "/cls_districts" ).permitAll() // TODO удалить, когда доступ нужно будет закрыть
                .antMatchers("/upload", "/uploadpart", "/upload/protocol", "/download/*").permitAll() // TODO удалить, когда доступ нужно будет закрыть
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/cabinet", true)
                .failureUrl("/login?error=true")
                .and()
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID");
    }
}