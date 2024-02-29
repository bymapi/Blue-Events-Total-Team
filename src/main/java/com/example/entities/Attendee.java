package com.example.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendees")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Attendee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "The name cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Write alphabetic letters only ")
    private String name;

    @NotEmpty(message = "The surname cannot be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Write alphabetic letters only ")
    private String surname;

    
    @Min(value = 10000, message = "The globalId must be greater than or equal to 10000")
    @Max(value = 999999999, message = " The globalId must be less than or equal to 999999999")
    @Column(name= "GlobalId", unique = true)
    private int globalId;

    @NotEmpty(message = "The field mail cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@blue\\.com$", message = "The mail should have the address @blue.com")
    @Column(name= "mail", unique = true)
    private String mail;


    @NotNull(message = "The profile cannot be empty")
    private Options options;

    @JsonIgnore
     @ManyToMany(fetch = FetchType.EAGER,
      cascade = {
          CascadeType.PERSIST,
          CascadeType.MERGE
      },
      mappedBy = "attendees")
      
    private Set<Event> events;

   
        

}
