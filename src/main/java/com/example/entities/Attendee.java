package com.example.entities;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Entity
@Table(name = "attendees")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Attendee implements Serializable {

        private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "prénom")
    @NotEmpty(message = "Le prénom ne peut pas être vide")
    private String name;

    @Column(name = "nom")
    @NotEmpty(message = "Le nom ne peut pas être vide")
    private String surname;

    @Column(name = "idGlobal", unique = true)
    @Min(value = 10000, message = "Le idGlobal doit être supérieur ou égal à 10000")
    @Max(value = 999999999, message = " Le idGlobal doit être inferieur ou égal à 10000")
    private int globalId;

    @Column(name = "email")
    @NotEmpty(message = "Le champ email ne peut pas être vide")
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@blue\\.com$", message = "L'adresse e-mail doit être @blue.com")
     private String mail;

    @Column(name = "prôfil")
    @NotNull(message = "Le prôfil ne peut pas être vide")
    private Profile profile;

    @JsonIgnore
     @ManyToMany(fetch = FetchType.EAGER,
      cascade = {
          CascadeType.PERSIST,
          CascadeType.MERGE
      },
      mappedBy = "attendees")
      
    private Set<Event> events;

   

}
