package com.example.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "The name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Write alphabetic letters only ")
    private String firstName;

    @NotEmpty(message = "The name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Write alphabetic letters only ")
    private String lastName;

    @Column(unique = true) // No se repite, pero no forma parte de la PK
    // @NaturalId(mutable = true) Para que forme parte de la PK
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@blue\\.com$", message = "The mail should have the address @blue.com")
    private String email;

    @NotEmpty(message = "The password cannot be empty")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
