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

    @NotEmpty(message = "The name cannot be empty")
    private String name;

    @NotEmpty(message = "The surname cannot be empty")
    private String surname;

    @Column(unique = true)
    @Min(value = 10000, message = "The globalId must be greater than or equal to 10000")
    @Max(value = 999999999, message = " The globalId must be less than or equal to 999999999")
    private int globalId;

    @NotEmpty(message = "The field mail cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@blue\\.com$", message = "The mail should have the address @blue.com")
    private String mail;


    @NotNull(message = "The profile cannot be empty")
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
