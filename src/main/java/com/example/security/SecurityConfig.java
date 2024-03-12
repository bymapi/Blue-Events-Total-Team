package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // The New Lambda DSL Syntax

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("utilisateurs/ajouter/utilisateur**").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/utilisateurs/ajouter/admin**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/event**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/participant**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/événements/{id}/inscription**").hasAnyAuthority("ADMIN", "UTILISATEUR");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/événement/{id}**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/participant/{globalId}**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT, "/api/événement/{id}**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT, "/api/participant/{globalId}**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT, "/api/événement/{id}/participants**").hasAuthority("ADMIN");//>-----Revisar
                    auth.requestMatchers(HttpMethod.GET,"/api/événements/disponibles**").hasAuthority( "ADMIN");
                    auth.requestMatchers(HttpMethod.GET,"/api/participant/{globalId}/disponible/événements**").hasAnyAuthority("UTILISATEUR", "ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/api/participant/{globalId}/événements**").hasAnyAuthority("UTILISATEUR","ADMIN");
                    auth.requestMatchers(HttpMethod.GET,"/api/participants**").hasAuthority( "ADMIN");
                    auth.anyRequest().authenticated();
                }).httpBasic(Customizer.withDefaults()).build();


                

     } 

}