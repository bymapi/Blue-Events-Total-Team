package com.example.entities;

import java.time.LocalDate;
import java.time.LocalTime;

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
public class EventDTOAdmin {

    @Column(name = "titre")
    private String title;

    private String description;

    @Column(name = "date_de_début")
    private LocalDate startDate;

    @Column(name = "date_de_fin")
    private LocalDate endDate;

    @Column(name = "heure_de_début")
    private LocalTime startTime;

    @Column(name = "heure_de_fin")
    private LocalTime endTime;

    private Mode mode;

    @Column(name = "Lieu")
    private String place;

    @Column(name = "statut_de_l'événement")
    private EventStatus eventStatus;

}
