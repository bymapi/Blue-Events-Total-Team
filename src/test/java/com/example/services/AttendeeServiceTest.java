package com.example.services;
import static org.assertj.core.api.Assertions.assertThat;
// Para seguir el enfoque de BDD con Mockito
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

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
import com.example.entities.Mode;
import com.example.entities.Options;
import com.example.entities.Target;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AttendeeServiceTest {


    @Mock
    private AttendeesDao attendeesDao;

    @Mock
    private EventsDao eventsDao;

    @InjectMocks
    private AttendeesServiceImpl attendeesServiceImpl;

    private Attendee attendee;

    @BeforeEach
    void setUp() {
        Event event1 = Event.builder()
            .title("French for non-native speakers")
            .target(Target.INTERNS)
            .description("French classes, level B2")
            .startDate(LocalDate.of(2024, 03, 10))
            .endDate(LocalDate.of(2024, 03, 10))
            .startTime(LocalTime.of(10, 30))
            .endTime(LocalTime.of(12, 30))
            .mode(Mode.ONLINE)
            .place("Blue offices, Valence")
            .build();

        attendee = Attendee.builder()
                .id(1)
                .name("Mariana")
                .surname("Urbina")
                .globalId(43166546)
                .mail("mariu@blue.com")
                .options(Options.BOOTCAMPER)
                //.events(event1)
                .build();
    }

    @Test
    @DisplayName("Test from repository to save an attendee")
    public void testSaveAttendee() {

        // given
        given(attendeesDao.save(attendee)).willReturn(attendee);

        // when
        Attendee attendeeSaved = attendeesDao.save(attendee);

        // then
        assertThat(attendeeSaved).isNotNull();
    }


    @DisplayName("Recover an empty list")
    @Test
    public void testEmptyProductList() {

        // given
        given(attendeesDao.findAll()).willReturn(Collections.emptyList());

        // when
        List<Attendee> attendees = attendeesDao.findAll();

        // then
        assertThat(attendees).isEmpty();
    }
}

