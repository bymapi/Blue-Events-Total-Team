package com.example.repository;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.dao.AttendeesDao;
import com.example.entities.Attendee;
import com.example.entities.Profile;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AttendeeRepositoryTest {

    @Autowired
    private AttendeesDao attendeesDao;

    private Attendee attendee0;

    @BeforeEach
    void setUp() {
         attendee0 = Attendee.builder()
                                .name("Carmen")
                                .surname("Perez")
                                .globalId(89101112)
                                .mail("Carmen@blue.com")
                                .profile(Profile.INTERNAL)
                                .build();
    }

    // Test para agregar un attendee
    @Test
    @DisplayName("Add attendee")
    public void testAddAttendee() {

        // given

        Attendee attendee1 = Attendee.builder()
                                .name("Andrea")
                                .surname("Gomez")
                                .globalId(654321)
                                .mail("Andrea@blue.com")
                                .profile(Profile.INTERNAL)
                                .build();

        // when

        Attendee attendeeAdded = attendeesDao.save(attendee1);

        // then

        assertThat(attendeeAdded).isNotNull();
        assertThat(attendeeAdded.getId()).isGreaterThan(0);

    }

     @DisplayName("Add attendee to a list")
    @Test
    public void testFindAllattendees() {

        // given
        Attendee attendee2 = Attendee.builder()
                                .name("Johanna")
                                .surname("Gomez")
                                .globalId(65432132)
                                .mail("Johanna@blue.com")
                                .profile(Profile.INTERNAL)
                                .build();
        /*  Attendee attendee3 = Attendee.builder()
                                .name("Juana")
                                .surname("Galindez")
                                .globalId(65432176)
                                .mail("Juana@blue.com")
                                .profile(Profile.INTERNAL)
                                .build(); */

        attendeesDao.save(attendee2);
        attendeesDao.save(attendee0);

        // Dado los empleados guardados
        // when
        List<Attendee> attendees = attendeesDao.findAll();

        // then
        assertThat(attendees).isNotNull();
        assertThat(attendees.size()).isEqualTo(2);
    } 

    @Test
    @DisplayName("Test para recuperar un attendee por su GlobalID")
    public void findUserByGlobalId() {

        // given

        attendeesDao.save(attendee0);

        // when

        Attendee attendee = attendeesDao.findByGlobalId(attendee0.getGlobalId());

        // then

        assertThat(attendee0.getGlobalId()).isNotEqualTo(0L);

    } 
//Comment
    @Test
    @DisplayName("Test to update an attendee")
    public void testUpdateAttendee() {

        // given

        attendeesDao.save(attendee0);

        // when

        Attendee attendeeSaved = attendeesDao.findByGlobalId(attendee0.getGlobalId());

        attendeeSaved.setSurname("Paez");
        attendeeSaved.setName("Almudena");
        attendeeSaved.setMail("jp@gg.com");

        Attendee attendeeUpdated = attendeesDao.save(attendeeSaved);

        // then

        assertThat(attendeeUpdated.getSurname()).isEqualTo("Paez");
        assertThat(attendeeUpdated.getMail()).isEqualTo("jp@gg.com");
        assertThat(attendeeUpdated.getName()).isEqualTo("Almudena");
    }

    @DisplayName("Test to delete an attendee")
    @Test
    public void testDeleteAttendee() {

        // given
        attendeesDao.save(attendee0);

        // when
        attendeesDao.delete(attendee0);
        Attendee  optionalAttendee = attendeesDao.findByGlobalId(attendee0.getGlobalId());

        // then
        assertThat(optionalAttendee).isNull();
    }
}
