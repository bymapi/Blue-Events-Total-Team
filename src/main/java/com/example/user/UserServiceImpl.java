package com.example.user;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.customExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User add(User user) {
        Optional<User> theUser = userRepository.findByEmail(user.getEmail());

        if(theUser.isPresent()) {
            // Deberiamos devolver una exception personalizada

            throw new ResourceNotFoundException("Il existe déjà un utilisateur avec cet e-mail");
        }

        // Encriptamos la password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository
            .findByEmail(email)
            .orElseThrow(() -> 
               new UsernameNotFoundException("Il n'existe aucun utilisateur avec l'adresse e-mail : " + email));
    }

    @Override
    public User update(User user) {

        User existingUser = userRepository.findByEmail(user.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("Il n'existe aucun utilisateur avec cet e-mail"));

            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            existingUser.setRole(user.getRole());

            User userUpdated = userRepository.save(existingUser);

            return userUpdated;
        
    }

}
