package com.example.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.example.customExceptions.ResourceNotFoundException;
import com.example.user.Role;
import com.example.user.User;
import com.example.user.UserRepository;
import com.example.user.UserServiceImpl;

// Mockito simula las depedencias, todas las dependencias van a ser simuladas.
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .firstName("Vict")
                .lastName("Rafael")
                .email("vrmachado@gmail.com")
                .password("Temp2023$$")
                .role(Role.ADMIN)
                .build();
    }

    // Test para guardar un user y que se genere una exception
    // Verifica que nunca sea posible agregar un empleado cuyo
    // email ya exista
    @Test
    @DisplayName("Test para guardar un user y genere una exception")
    public void testSaveUserWithThrowException() {

        // given
        given(userRepository.findByEmail(user.getEmail()))
                .willReturn(Optional.of(user));

        // when
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.add(user);
        });

        // Then
        verify(userRepository, never()).save(any(User.class));

    }

}
