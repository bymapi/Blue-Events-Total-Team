package com.example.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
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

    // @Size(min = 5, max = 9, message = "the number of characters of this id cannot
    // be less than 5 or more than 9")
    private int globalId;

    @NotEmpty(message = "The field mail cannot be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@blue\\.com$", message = "The mail should have the address @blue.com")
    private String mail;

    @NotEmpty(message = "The profile cannot be empty")
    private Options options;

    @JsonIgnore
     @ManyToMany(fetch = FetchType.EAGER,
      cascade = {
          CascadeType.PERSIST,
          CascadeType.MERGE
      },
      mappedBy = "attendees")
      
    private List<Event> events;

    // public void addAttendees(Attendee attendee){
    //     this.attendees.add(attendee);
    //     attendee.getEvents().add(this);
    //     }
    
    //     public void removeAttendee(int attendeeId){
    //         Attendee attendee = this.attendees.stream().filter(e -> e.getId() == attendeeId).findFirst().findFirst().orElse(null);
    //         if (attendee != null) {
    //             this.attendees.remove(attendee);
    //             attendee.getEvents().remove(this);
                
    //         }
    //     }

}
