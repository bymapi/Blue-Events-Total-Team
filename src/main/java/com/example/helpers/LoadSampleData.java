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
import com.example.user.Role;
import com.example.user.User;
import com.example.user.UserService;

@Configuration
public class LoadSampleData {

    @Bean
    public CommandLineRunner initSampleData(EventsService eventsService, AttendeesService attendeesService,
            UserService userService) {

        return data -> {

            Event event1 = eventsService.eventSaved(Event.builder()
                    .title("French for non-native speakers")
                    .target(Target.STAGIAIRES)
                    .description("French classes, level B2")
                    .startDate(LocalDate.of(2024, 03, 10))
                    .endDate(LocalDate.of(2024, 03, 10))
                    .startTime(LocalTime.of(10, 30))
                    .endTime(LocalTime.of(12, 30))
                    .mode(Mode.EN_LIGNE)
                    .place("Blue offices, Valence")
                    .eventStatus(EventStatus.ACTIVÉ)
                    .attendees(new HashSet<>())
                    .build());

                    Event event2 = eventsService.eventSaved(Event.builder()
                    .title("Pets and owners get-together")
                    .target(Target.STAGIAIRES)
                    .description("Social event aming to get to know each other and make new friends, with two or even more legs")
                    .startDate(LocalDate.of(2024, 03, 22))
                    .endDate(LocalDate.of(2024, 03, 22))
                    .startTime(LocalTime.of(10, 30))
                    .endTime(LocalTime.of(12, 30))
                    .mode(Mode.MiMIXTE)
                    .place("Blue offices, Valence")
                    .eventStatus(EventStatus.ACTIVÉ)
                    .attendees(new HashSet<>())
                    .build());

                    Event event3 = eventsService.eventSaved(Event.builder()
                    .title("Hackaton")
                    .target(Target.STAGIAIRES)
                    .description("Hackathon, first edition")
                    .startDate(LocalDate.of(2024, 03, 25))
                    .endDate(LocalDate.of(2024, 03, 25))
                    .startTime(LocalTime.of(10, 30))
                    .endTime(LocalTime.of(12, 30))
                    .mode(Mode.PRÉSENTIEL)
                    .place("Blue offices, Valence")
                    .eventStatus(EventStatus.ACTIVÉ)
                    .attendees(new HashSet<>())
                    .build());

            Attendee attendee1 = attendeesService.save(Attendee.builder()
                    .name("Alfredo")
                    .surname("Adame")
                    .globalId(10808939)
                    .mail("alfrea@blue.com")
                    .profile(Profile.BOOTCAMP)
                    .events(new HashSet<>())
                    .build());

            event1.addAttendees(attendee1);
            eventsService.eventSaved(event1);

            User userAdmin = userService.add(User.builder().firstName("Andrea").lastName("Gomez")
                    .email("Admin1@blue.com").password("PASSWORD1").role(Role.ADMIN).build());

                    User userUser = userService.add(User.builder().firstName("Jesus").lastName("Baraza")
                    .email("user1@blue.com").password("PASSWORD2").role(Role.UTILISATEUR).build());
        };
    }

}
