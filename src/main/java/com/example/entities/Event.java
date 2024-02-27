package com.example.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Table(name = "events")

public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // @NotNull(message = "Must not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Write alphabetic letters only ")
    private String title;

    // @NotNull
    @Column(name = "campo_no_modificable", updatable = false)
    private Target target;

    // @NotNull(message = "Must not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Write alphabetic letters only ")
    private String description;

    // // @NotNull(message = "Must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    // // @NotNull(message = "Must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    // @NotNull(message = "Must not be empty")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    // @NotNull(message = "Must not be empty")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    // @NotNull(message = "Must not be empty")
    private Mode mode;

    // A preguntar pero de momento lo pongo como String
    // @NotNull(message = "Must not be empty")
    private String place;

    @ManyToMany(fetch = FetchType.EAGER,
    cascade = {CascadeType.PERSIST,
        CascadeType.MERGE})
  
   @JoinTable(name = "events_attendees",
       joinColumns = { @JoinColumn(name = "id_event") },
       inverseJoinColumns = { @JoinColumn(name = "id_attendee") })

       private Set<Attendee> attendees;
  
   


    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE })

    @JoinTable(name = "events_attendees", joinColumns = { @JoinColumn(name = "id_event") }, inverseJoinColumns = {
            @JoinColumn(name = "id_attendee") })

    private Set<Attendee> attendees;

//     public List<Event> events;

    // public Event(String title, Target target, String description,
    //         LocalDate startDate, LocalTime startTime,
    //         LocalDate endDate, LocalTime endTime,
    //         Mode mode, String place) {

    //     this.title = title;
    //     this.target = target;
    //     this.description = description;
    //     this.startDate = startDate;
    //     this.startTime = startTime;
    //     this.endDate = endDate;
    //     this.endTime = endTime;
    //     this.mode = mode;
    //     this.place = place;
    // };

    // public List<Event> getEvents() { <-----Redundante ignorar
    //     return events;
    // }

    public void addAttendees(Attendee attendee){
        this.attendees.add(attendee);
        attendee.getEvents().add(this);
        }
    
        public void removeAttendee(int attendeeId){
            Attendee attendee = this.attendees.stream()
            .filter(e -> e.getId() == attendeeId).findFirst().orElse(null);
            if (attendee != null) {
                this.attendees.remove(attendee);
                attendee.getEvents().remove(this);
                
            }
        }

}