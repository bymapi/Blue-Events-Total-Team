package com.example.helpers;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.example.entities.Event;
import com.example.entities.Mode;
import com.example.entities.Target;
import com.example.services.EventsService;

@Configuration
public class LoadSampleData {

    public CommandLineRunner saveSampleData(EventsService eventsService) {

        return datos -> {

            eventsService.eventSaved(Event.builder()
                    .title("French for non-native speakers")
                    .target(Target.INTERNS)
                    .description("French classes, level B2")
                    .startDate(LocalDate.of(2024, 03, 10))
                    .endDate(LocalDate.of(2024, 03, 10))
                    .startTime(LocalTime.of(10, 30))
                    .endTime(LocalTime.of(12, 30))
                    .mode(Mode.ONLINE)
                    .place("Blue offices, Valence")
                    // .attendees(attendeesService.findById(1))
                    .build());

        };
    }

}
