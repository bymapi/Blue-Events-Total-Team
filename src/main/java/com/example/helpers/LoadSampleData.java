package com.example.helpers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.entities.Attendee;
import com.example.entities.Event;
import com.example.entities.EventStatus;
import com.example.entities.Mode;
import com.example.entities.Profile;
import com.example.entities.Target;
import com.example.services.AttendeesService;
import com.example.services.EventsService;

@Configuration
public class LoadSampleData {

    @Bean
    public CommandLineRunner initSampleData(EventsService eventsService, AttendeesService attendeesService) {

        return data -> {

            Event event1 = eventsService.eventSaved(Event.builder()
                    .title("French for non-native speakers")
                    .target(Target.INTERNS)
                    .description("French classes, level B2")
                    .startDate(LocalDate.of(2024, 03, 10))
                    .endDate(LocalDate.of(2024, 03, 10))
                    .startTime(LocalTime.of(10, 30))
                    .endTime(LocalTime.of(12, 30))
                    .mode(Mode.ONLINE)
                    .place("Blue offices, Valence")
                    .eventStatus(EventStatus.ENABLE)
                    .attendees(new HashSet<>())
                    .build());

           Attendee attendee1 = attendeesService.save(Attendee.builder()
                    .name("Alfredo")
                    .surname("Adame")
                    .globalId(10808939)
                    .mail("alfrea@blue.com")
                    .profile(Profile.BOOTCAMPER)
                    .events(new HashSet<>())
                    .build());

            event1.addAttendees(attendee1);
            eventsService.eventSaved(event1);
        

        };
    }

}
