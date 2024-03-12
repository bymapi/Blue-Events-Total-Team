package com.example.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.example.dao.AttendeesDao;
import com.example.dao.EventsDao;
import com.example.entities.Attendee;
import com.example.entities.Event;
import com.example.entities.EventStatus;
import com.example.entities.Mode;
import com.example.entities.Profile;
import com.example.entities.Target;

import static org.assertj.core.api.Assertions.assertThat;

// Para seguir el enfoque de BDD con Mockito
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class EventServiceTests {

    @Mock
    private EventsDao eventDao;

    @Mock
    private AttendeesDao attendeeDao;

    @InjectMocks
    private EventsServiceImpl eventService;

    private Event event;

    @BeforeEach
    void setUp() {
        Attendee attendee1 = Attendee.builder()
                .name("Maricarmen")
                .surname("Gomez")
                .globalId(2765)
                .mail("Maricarmen@blue.com")
                .profile(Profile.INTERNE)
                .build();

        Attendee attendee2 = Attendee.builder()
                .name("Maricarmen")
                .surname("Gomez")
                .globalId(2765)
                .mail("Maricarmen@blue.com")
                .profile(Profile.INTERNE)
                .build();

        Event event1 = Event.builder()
                // .id(20)
                .title("Google Pixel 7")
                .target(Target.STAGIAIRES)
                .description("Evento para conocer gente")
                .startDate(LocalDate.of(2024, 04, 10))
                .endDate(LocalDate.of(2024, 05, 10))
                .startTime(LocalTime.of(12, 10))
                .endTime(LocalTime.of(15, 30))
                .eventStatus(EventStatus.ACTIVÉ)
                .mode(Mode.EN_LIGNE)
                .place("Valencia")
                // .Attendee(attendee)
                .build();

        event1.setAttendees(Set.of(attendee1,attendee2));

    }

    @Test
    @DisplayName("Test de servicio para persistir un Event")
    public void testGuardarEvent() {

        Attendee attendee01 = Attendee.builder()
                .name("Maricarmen")
                .surname("Gomez")
                .globalId(2765)
                .mail("Maricarmen@blue.com")
                .profile(Profile.INTERNE)
                .build();

        Attendee attendee02 = Attendee.builder()
                .name("Maricarmen")
                .surname("Gomez")
                .globalId(2765)
                .mail("Maricarmen@blue.com")
                .profile(Profile.INTERNE)
                .build();

        Event event0 = Event.builder()
                // .id(20)
                .title("Google Pixel 7")
                .target(Target.STAGIAIRES)
                .description("Evento para conocer gente")
                .startDate(LocalDate.of(2024, 04, 10))
                .endDate(LocalDate.of(2024, 05, 10))
                .startTime(LocalTime.of(12, 10))
                .endTime(LocalTime.of(15, 30))
                .eventStatus(EventStatus.ACTIVÉ)
                .mode(Mode.EN_LIGNE)
                .place("Valencia")
                // .Attendee(attendee)
                .build();

        event0.setAttendees(Set.of(attendee01,attendee02));

        // given
        given(eventDao.save(event0)).willReturn(event0);

        // when
        Event eventGuardado = eventService.eventSaved(event0);

        // then
        assertThat(eventGuardado).isNotNull();
    }

    @DisplayName("Recupera una lista vacia de Events")
    @Test
    public void testEmptyProductList() {

        // given
        given(eventDao.findAll()).willReturn(Collections.emptyList());

        // when
        List<Event> events = eventDao.findAll();

        // then
        assertThat(events).isEmpty();
    }
}
