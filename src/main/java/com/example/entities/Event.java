package com.example.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
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

    @NotNull(message = "Must not be empty")
    private String title;

    @NotNull
    private Target target;

    @NotNull(message = "Must not be empty")
    private String description;

    @NotNull(message = "Must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "Must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Must not be empty")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "Must not be empty")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull(message = "Must not be empty")   
    private EventStatus eventStatus;

    @NotNull(message = "Must not be empty")
    private Mode mode;

    @NotNull(message = "Must not be empty")
    private String place;

    private final int maximumNumberOfAttendees = 8;

    @NotNull
    private final int maximumNumberOfAttendees = 8;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE })

    @JoinTable(name = "events_attendees", joinColumns = {
            @JoinColumn(name = "id_event") }, inverseJoinColumns = {
                    @JoinColumn(name = "id_attendee") })

    private Set<Attendee> attendees = new HashSet<>();

// Porque se pone esta lista aqui?
    public List<Event> events;

    public void addAttendees(Attendee attendee) {
        this.attendees.add(attendee);
        attendee.getEvents().add(this);
    }

    public void removeAttendee(int attendeeId) {
        Attendee attendee = this.attendees.stream().filter(e -> e.getId() == attendeeId).findFirst().orElse(null);
        if (attendee != null) {
            this.attendees.remove(attendee);
            attendee.getEvents().remove(this);

        }
    }

}