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

            throw new ResourceNotFoundException("Ya existe un user con dicho email");
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
               new UsernameNotFoundException("No existe el usuario con el email: " + email));
    }

    @Override
    public User update(User user) {

        User existingUser = userRepository.findByEmail(user.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("No existe user con dicho email"));

            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            existingUser.setRole(user.getRole());

            User userUpdated = userRepository.save(existingUser);

            return userUpdated;
        
    }

}
