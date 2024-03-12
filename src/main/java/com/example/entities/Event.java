package com.example.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;

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

    @Column(name = "titre")
    @NotNull(message = "Le titre ne peut pas être vide")
    private String title;

    @Column(name = "cible")
    @NotNull
    private Target target;

    
    @NotNull(message = "La descriptionl ne peut pas être vide")
    private String description;

    @Column(name = "date_de_début")
    @NotNull(message = "La date de début ne peut pas être vide")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "date_de_fin")
    @NotNull(message = "La date de fin ne peut pas être vide")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(name = "heure_de_début")
    @NotNull(message = "L'heure de début ne peut pas être vide")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Column(name = "heure_de_fin")
    @NotNull(message = "L'heure de fin ne peut pas être vide")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Column(name = "statut_de_l'événement")
    @NotNull(message = "L'statut_de_l'événement ne peut pas être vide")   
    private EventStatus eventStatus;

    
    @NotNull(message = "Le mode ne peut pas être vide")
    private Mode mode;

    @Column(name = "lieu")
    @NotNull(message = "Le place ne peut pas être vide")
    private String place;

    @Column(name = "image")
    private String imagen;

    @Column(name = "nombre_maximum_de_participantst")
    private final int maximumNumberOfAttendees = 8;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE })
    @JoinTable(name = "events_attendees", joinColumns = {
            @JoinColumn(name = "id_event") }, inverseJoinColumns = {
                    @JoinColumn(name = "id_attendee") })
    private Set<Attendee> attendees = new HashSet<>();



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
