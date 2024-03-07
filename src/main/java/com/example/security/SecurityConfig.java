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
                    auth.requestMatchers("/users/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/api/events**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/attendee**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/events/{id}/register**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/events**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/attendee/{globalId}**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT, "/api/events**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT, "/api/attendee/{globalId}**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT, "/api/event/{id}/attendees**").hasAuthority("ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/api/attendee/{globalId}/events**").hasAnyAuthority("USER","ADMIN");
                    auth.requestMatchers(HttpMethod.GET,"/api/events/available**").hasAnyAuthority("USER", "ADMIN");
                    auth.requestMatchers(HttpMethod.GET,"/api/attendees**").hasAuthority( "ADMIN");
                    auth.anyRequest().authenticated();
                }).httpBasic(Customizer.withDefaults()).build();


                

     } 

}
