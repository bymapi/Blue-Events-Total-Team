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

    @Column(name = "prénom")
    private String firstName;

    @Column(name = "nom")
    private String lastName;

    @Column(unique = true)
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@blue\\.com$", message = "L'adresse email devrait être @blue.com")
    private String email;

    @Column(name= "mot de passe")
    private String password;

    @Column(name = "rôle")
    @Enumerated(EnumType.STRING)
    private Role role;

}
