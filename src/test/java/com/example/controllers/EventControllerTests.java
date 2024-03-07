package com.example.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.example.entities.Event;
import com.example.entities.EventStatus;
import com.example.entities.Mode;
import com.example.entities.Profile;
import com.example.entities.Target;
import com.example.helpers.FileDownload;
import com.example.helpers.FileUpLoad;
import com.example.entities.Attendee;

import com.example.services.EventsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc

@AutoConfigureTestDatabase(replace = Replace.NONE)

public class EventControllerTests {

    @Autowired
    private MockMvc mockMvc; // Simular peticiones HTTP

    @MockBean
    private EventsService eventsService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FileUpLoad fileUpload;

    @MockBean
    private FileDownload fileDownload;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Test de intento de guardar un evento sin autorizacion")
    // @WithMockUser(username = "gabybcr1542@blue.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}

    // given
    void testGuardarEvento() throws Exception {
        // crear un evento
        Event event1 = Event.builder()
                .title("BootCampers")
                .target(Target.INTERNS)
                .description("Ecuentro de todas las Bootcampers")
                .startDate(LocalDate.of(2024, 05, 10))
                .endDate(LocalDate.of(2024, 05, 10))
                .startTime(LocalTime.of(10, 10))
                .endTime(LocalTime.of(12, 30))
                .eventStatus(EventStatus.ENABLE)
                .mode(Mode.MIXED)
                .place("Valencia")
                .build();

        Event event2 = Event.builder()
                .title("Intercambio Cultural")
                .target(Target.INTERNS)
                .description("Ecuentro Cultural")
                .startDate(LocalDate.of(2024, 06, 11))
                .endDate(LocalDate.of(2024, 06, 11))
                .startTime(LocalTime.of(15, 10))
                .endTime(LocalTime.of(18, 30))
                .eventStatus(EventStatus.ENABLE)
                .mode(Mode.PRESENTIAL)
                .place("Valencia")
                .build();

        // Crear Attendee

        Attendee attendee1 = Attendee.builder()
                .name("Dunia")
                .surname("Maria")
                .globalId(276579)
                .mail("lab@blue.com")
                .profile(Profile.INTERNAL)
                .build();

        attendee1.setEvents(Set.of(event1, event2));

        String jsonStringEvent = objectMapper.writeValueAsString(event1);

        MockMultipartFile bytesArrayEvent = new MockMultipartFile("event",
                null, "application/json", jsonStringEvent.getBytes());

        // multipart: perfoms a POST request
        mockMvc.perform(multipart("/api")
                .file("file", null)
                .file(bytesArrayEvent))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }



    @Test
    @DisplayName("Test de intento de guardar un evento sin autorizacion")
    @WithMockUser(username = "gabybcr1542@blue.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}

    // given
    void testGuardarEventoAutorizado() throws Exception {
        // crear un evento
        Event event1 = Event.builder()
                .title("BootCampers")
                .target(Target.INTERNS)
                .description("Ecuentro de todas las Bootcampers")
                .startDate(LocalDate.of(2024, 05, 10))
                .endDate(LocalDate.of(2024, 05, 10))
                .startTime(LocalTime.of(10, 10))
                .endTime(LocalTime.of(12, 30))
                .eventStatus(EventStatus.ENABLE)
                .mode(Mode.MIXED)
                .place("Valencia")
                .build();

        Event event2 = Event.builder()
                .title("Intercambio Cultural")
                .target(Target.INTERNS)
                .description("Ecuentro Cultural")
                .startDate(LocalDate.of(2024, 06, 11))
                .endDate(LocalDate.of(2024, 06, 11))
                .startTime(LocalTime.of(15, 10))
                .endTime(LocalTime.of(18, 30))
                .eventStatus(EventStatus.ENABLE)
                .mode(Mode.PRESENTIAL)
                .place("Valencia")
                .build();

        // Crear Attendee

        Attendee attendee1 = Attendee.builder()
                .name("Dunia")
                .surname("Maria")
                .globalId(276579)
                .mail("lab@blue.com")
                .profile(Profile.INTERNAL)
                .build();

        attendee1.setEvents(Set.of(event1, event2));

        String jsonStringEvent = objectMapper.writeValueAsString(event1);

        MockMultipartFile bytesArrayEvent = new MockMultipartFile("event",
                null, "application/json", jsonStringEvent.getBytes());

        // multipart: perfoms a POST request
        mockMvc.perform(multipart("/api")
                .file("file", null)
                .file(bytesArrayEvent))
                .andDo(print())
                .andExpect(status().isCreated());

    }





    
}
