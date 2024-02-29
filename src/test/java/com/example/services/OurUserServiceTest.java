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

import com.example.exception.ResourceNotFoundException;
import com.example.security.entities.OurUser;
import com.example.security.entities.Role;
import com.example.security.repository.OurUserRepository;
import com.example.security.services.OurUserDetailsService;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class OurUserServiceTest {

@Mock
    private OurUserRepository ourUserRepository;

    @InjectMocks
    private OurUserDetailsService ourUserDetailsService;

    private OurUser ourUser;

    @BeforeEach
    void setUp() {
        ourUser = OurUser.builder()
                .email("admin1@blue.com")
                .password("Temp2023$$")
                .role(Role.ADMIN)
                .build();
    }

    @Test
    @DisplayName("Test to save an user and generate an exception")
    public void testSaveUserWithThrowException() {

        // given
        given(ourUserRepository.findByEmail(ourUser.getEmail()))
                .willReturn(Optional.of(ourUser));

        // when
        assertThrows(ResourceNotFoundException.class, () -> {
            ourUserDetailsService.add(ourUser);
        });

        // Then
        verify(ourUserRepository, never()).save(any(OurUser.class));

    }

}
