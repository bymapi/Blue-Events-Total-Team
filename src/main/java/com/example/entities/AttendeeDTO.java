package com.example.entities;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeDTO {

    @Column(name = "prénom")
    private String name;

    @Column(name = "nom")
    private String surname;

    @Column(name = "idGlobal")
    private int globalId;

    @Column(name = "email")
    private String mail;

    @Column(name = "profil")
    private Profile profile;

}
