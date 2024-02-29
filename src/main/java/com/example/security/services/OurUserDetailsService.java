package com.example.security.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.exception.ResourceNotFoundException;
import com.example.security.entities.OurUser;
import com.example.security.repository.OurUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OurUserDetailsService implements UserDetailsService {

    @Autowired
    private OurUserRepository ourUserRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return ourUserRepository.findByEmail(username).orElseThrow();
    }

    
    public OurUser add(OurUser ourUser) {
        Optional<OurUser> theUser = ourUserRepository.findByEmail(ourUser.getEmail());

        if(theUser.isPresent()) {
            
            // Deberiamos devolver una exception personalizada

            throw new ResourceNotFoundException("An user with this same email address already exists");
        }
        // Para poder usar el metodo passwordEncoder empelamos BcriptPassword encoder, 
        //creamos el objeto llamandolo y usandolo de inmediato, para poder usar el metodo encode

        // // Encriptamos la password
        ourUser.setPassword(new BCryptPasswordEncoder().encode(ourUser.getPassword()));
        return ourUserRepository.save(ourUser);
    }

}