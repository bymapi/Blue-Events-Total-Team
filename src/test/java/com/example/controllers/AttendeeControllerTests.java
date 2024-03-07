package com.example.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.entities.Attendee;
import com.example.entities.Event;
import com.example.entities.EventStatus;
import com.example.entities.Mode;
import com.example.entities.Profile;
import com.example.entities.Target;
import com.example.services.AttendeesService;
import com.example.services.EventsService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

/**
 * @WebMvcTest. 
 * 
 * Con esta anotacion seria suficiente, si no tuviesemos configurado Spring Security,
 * porque permitiria cargar el controlador especifico y sus dependencias sin tener que
 * cargar todo el contexto de la aplicacion, y aqui es donde tendriamos un problema con
 * Spring Security, pues si necesitamos levantar todo el contexto de la aplicacion.
 *
 * Tambien permite autoconfigurar MockMvc para realizar test a los controladores, es
 * decir, peticiones HTTP a los end points.
 */
@Transactional
@SpringBootTest
@AutoConfigureMockMvc // Test a los controladores, a los end points, teniendo Spring Securiy
                      // configurado
// @ContextConfiguration(classes = SecurityConfig.class)
// @WebAppConfiguration
@AutoConfigureTestDatabase(replace = Replace.NONE)
// @WithMockUser(username = "vrmachado@gmail.com",
// authorities = {"ADMIN", "USER"})
// @WithMockUser(roles="ADMIN") - Error 403
public class AttendeeControllerTests {

        @Autowired
        private MockMvc mockMvc; // Simular peticiones HTTP

        // Permite agregar objetos simulados al contexto de la aplicacion.
        // El simulacro o simulacion va a remplazar cualquier bean existente
        // en el contexto de la aplicacion.
        @MockBean
        private AttendeesService attendeesService;

        @MockBean
        private EventsService eventsService;

        @Autowired
        private ObjectMapper objectMapper;


        @Autowired
        private WebApplicationContext context;


        private Attendee attendee0;

        private Event event0;

        @BeforeEach
        public void setUp() {
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

        attendee0 = Attendee.builder()
                .name("Carolina")
                .surname("Colomina")
                .globalId(276590)
                .mail("Carolina@blue.com")
                .profile(Profile.INTERNAL)
                .build();

        event0 = Event.builder()
                .title("Language exchange")
                .target(Target.INTERNS)
                .description("Evento para conocer gente")
                .startDate(LocalDate.of(2024, 04, 10))
                .endDate(LocalDate.of(2024, 05, 10))
                .startTime(LocalTime.of(12, 10))
                .endTime(LocalTime.of(15, 30))
                .eventStatus(EventStatus.ENABLE)
                .mode(Mode.ONLINE)
                .place("Valencia")
                .build();
        }

        
        @DisplayName("Test de intento de guardar un attendee sin autorizacion")
        @Test
        @WithMockUser(username = "Rosa@blue.com", authorities = { "USER" }) 

        void testGuardarAttendee() throws Exception {
        //given
        Attendee attendee01 = Attendee.builder()
                .name("Maricarmen")
                .surname("Gomez")
                .globalId(276590)
                .mail("Maricarmen@blue.com")
                .profile(Profile.INTERNAL)
                .build();

       
        Event event1 = Event.builder()
                .title("Google Pixel 7")
                .target(Target.INTERNS)
                .description("Evento para conocer gente")
                .startDate(LocalDate.of(2024, 04, 10))
                .endDate(LocalDate.of(2024, 05, 10))
                .startTime(LocalTime.of(12, 10))
                .endTime(LocalTime.of(15, 30))
                .eventStatus(EventStatus.ENABLE)
                .mode(Mode.ONLINE)
                .place("Valencia")
                .build();

        attendee01.setEvents(Set.of(event0,event1));

               String jsonStringAttendee = objectMapper.writeValueAsString(attendee01);

                mockMvc.perform(post("/api/attendee")
                .contentType("application/json")
                .content(jsonStringAttendee))
                .andDo(print())
                .andExpect(status().isForbidden());

        }


        @DisplayName("Test guardar attendee con Admin ")
        @Test
        @WithMockUser(username = "Mapi@blue.com", authorities = { "ADMIN" }) 
        
        void testGuardarAttendeeConUserMocked() throws Exception {
                // given
                Event eventA = Event.builder()
                .title("Language exchange")
                .target(Target.INTERNS)
                .description("Evento para conocer gente")
                .startDate(LocalDate.of(2024, 04, 10))
                .endDate(LocalDate.of(2024, 05, 10))
                .startTime(LocalTime.of(12, 10))
                .endTime(LocalTime.of(15, 30))
                .eventStatus(EventStatus.ENABLE)
                .mode(Mode.ONLINE)
                .place("Valencia")
                .build();

                attendee0.setEvents(Set.of(event0, eventA));

                String jsonStringAttendee = objectMapper.writeValueAsString(attendee0);
 
                 mockMvc.perform(post("/api/attendee")
                 .contentType("application/json")
                 .content(jsonStringAttendee))
                 .andDo(print())
                 .andExpect(status().isCreated());
                      }

                
        @DisplayName("Test de obtener una lista de attendees con Admin mockeado")
        @Test
        @WithMockUser(username = "mapi@blue.com", authorities = { "ADMIN" }) 
        public void testListaAttendees() throws Exception {

                // given

                List<Attendee> attendees = new ArrayList<>();


                Event event5 = Event.builder()
                                .title("Google Pixel 7")
                                .target(Target.INTERNS)
                                .description("Evento para conocer gente")
                                .startDate(LocalDate.of(2024, 04, 10))
                                .endDate(LocalDate.of(2024, 05, 10))
                                .startTime(LocalTime.of(12, 10))
                                .endTime(LocalTime.of(15, 30))
                                .eventStatus(EventStatus.ENABLE)
                                .mode(Mode.ONLINE)
                                .place("Valencia")
                                .build();

                Attendee attendee5 = Attendee.builder()
                                .name("Maricarmen")
                                .surname("Gomez")
                                .globalId(1)
                                .mail("Maricarmen@blue.com")
                                .profile(Profile.INTERNAL)
                                .build();
                                                
                attendee5.setEvents(Set.of(event0, event5));

                Event event6 = Event.builder()
                                .title("Google Pixel 7")
                                .target(Target.INTERNS)
                                .description("Evento para conocer gente")
                                .startDate(LocalDate.of(2024, 04, 10))
                                .endDate(LocalDate.of(2024, 05, 10))
                                .startTime(LocalTime.of(12, 10))
                                .endTime(LocalTime.of(15, 30))
                                .eventStatus(EventStatus.ENABLE)
                                .mode(Mode.ONLINE)
                                .place("Valencia")
                                .build();

                Attendee attendee6 = Attendee.builder()
                                .name("Gloria")
                                .surname("Gomez")
                                .globalId(1)
                                .mail("Gloria@blue.com")
                                .profile(Profile.INTERNAL)
                                .build();
                                                
                attendee6.setEvents(Set.of(event0, event5, event6)); 


                        attendees.add(attendee5);
                        attendees.add(attendee6);
                        attendees.add(attendee0);

        when(attendeesService.findAllAttendees()).thenReturn(attendees);

        // When
         mockMvc.perform(get("/api/attendees"))
            // Then
            .andExpect(status().isOk())
                                .andDo(print())
                                .andExpect(jsonPath("$.size()", is(attendees.size())));

        }

        @DisplayName("Test actualizar un attendee con Admin mockeado")
        @Test
        @WithMockUser(username = "vrmachado@blue.com", authorities = { "ADMIN" }) 
        public void testActualizarAttendee() throws Exception {

                // given

            

                Event eventGuardado = Event.builder()
                                .title("Google Pixel 7")
                                .target(Target.INTERNS)
                                .description("Evento para conocer gente")
                                .startDate(LocalDate.of(2024, 04, 10))
                                .endDate(LocalDate.of(2024, 05, 10))
                                .startTime(LocalTime.of(12, 10))
                                .endTime(LocalTime.of(15, 30))
                                .eventStatus(EventStatus.ENABLE)
                                .mode(Mode.ONLINE)
                                .place("Valencia")
                                .build();

                Attendee attendeeGuardado = Attendee.builder()
                                .name("Maricarmen")
                                .surname("Gomez")
                                .globalId(1678912)
                                .mail("Maricarmen@blue.com")
                                .profile(Profile.INTERNAL)
                                .build();
                                                
                 attendeeGuardado.setEvents(Set.of(eventGuardado));        

               
                Attendee attendeeActualizado= Attendee.builder()
                                .name("Gerania")
                                .surname("Gomez")
                                .globalId(1678912)
                                .mail("Maricarmen@blue.com")
                                .profile(Profile.INTERNAL)
                                .build();
                                                
                attendeeActualizado.setEvents(Set.of(event0));

                                

                given(attendeesService.findByGlobalId(attendeeGuardado.getGlobalId())).willReturn(attendeeGuardado);
                                
                given(attendeesService.save(any(Attendee.class)))
                                .willAnswer(invocation -> invocation.getArgument(0));

                // when

                ResultActions response = mockMvc.perform(put("/api/attendee/{globalId}", attendeeGuardado.getGlobalId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(attendeeActualizado)).accept(MediaType.APPLICATION_JSON));

                // then

                response.andExpect(status().isOk());

        }

        
        @DisplayName("Test eliminar un attendee con Admin mockeado")
        @Test
        @WithMockUser(username = "vrmachado@blue.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}
        public void testDeleteAttendeeByIdGlobal() throws Exception {

                // given

               Attendee attendee8 = Attendee.builder()
                .name("Gonzalo")
                .surname("Galindez")
                .globalId(1654231)
                .mail("Gonzalo@blue.com")
                .profile(Profile.INTERNAL)
                .build();

                given(attendeesService.findByGlobalId(attendee8.getGlobalId()))
                                .willReturn(attendee8);

                willDoNothing().given(attendeesService).delete(attendee8);

                // when

                ResultActions response = mockMvc.perform(delete("/api/attendee/{globalId}", attendee8.getGlobalId())
                .accept(MediaType.APPLICATION_JSON));

                // then

                response.andExpect(status().isOk())
                                .andDo(print());

        }


        @DisplayName("Test de obtener una lista de eventos de un attendee con Admin mockeado")
        @Test
        @WithMockUser(username = "mapi@blue.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}
        public void testListaEventsAttendees() throws Exception {

                // given
                Integer globalId = 1;

                
                Event event11 = Event.builder()
                                .title("Google Pixel 7")
                                .target(Target.INTERNS)
                                .description("Evento para conocer gente")
                                .startDate(LocalDate.of(2024, 04, 10))
                                .endDate(LocalDate.of(2024, 05, 10))
                                .startTime(LocalTime.of(12, 10))
                                .endTime(LocalTime.of(15, 30))
                                .eventStatus(EventStatus.ENABLE)
                                .mode(Mode.ONLINE)
                                .place("Valencia")
                                .build();

                Event event12 = Event.builder()
                                .title("Google Pixel 7")
                                .target(Target.INTERNS)
                                .description("Evento para conocer gente")
                                .startDate(LocalDate.of(2024, 04, 10))
                                .endDate(LocalDate.of(2024, 05, 10))
                                .startTime(LocalTime.of(12, 10))
                                .endTime(LocalTime.of(15, 30))
                                .eventStatus(EventStatus.ENABLE)
                                .mode(Mode.ONLINE)
                                .place("Valencia")
                                .build();

                Attendee attendee11 = Attendee.builder()
                                .name("Gloria")
                                .surname("Gomez")
                                .globalId(1)
                                .mail("Gloria@blue.com")
                                .profile(Profile.INTERNAL)
                                .build();
                                                
                 attendee11.setEvents(Set.of(event11,event12)); 
                 List<Event> allAttendeeEvents = List.of(event11, event12);
                 when(eventsService.findEventsByAttendeeGlobalId(globalId)).thenReturn(allAttendeeEvents);
         
                 // When
                 mockMvc.perform(get("/api/attendee/{globalId}/events", globalId))
                     // Then
                     .andExpect(status().isOk())
                     .andDo(print())
                     .andExpect(jsonPath("$.size()", is(allAttendeeEvents.size())));

        }       

           
        

     /* @DisplayName("Test Recuperar un Attendee por el id")
        @Test
        @WithMockUser(username = "Clem@blue.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}
        public void testRecuperarProductoPorId() throws Exception {
                // given
                int globalId = 1;

                Attendee attendee9 = Attendee.builder()
                .name("Maricarmen")
                .surname("Gomez")
                .globalId(1)
                .mail("Maricarmen@blue.com")
                .profile(Profile.INTERNAL)
                .build();

                given(attendeesService.findByGlobalId(globalId))
                                .willReturn(attendee9);

                // when

                ResultActions response = mockMvc.perform(get("/productos/{id}", productoId));

                // then

                response.andExpect(status().isOk())
                                .andDo(print())
                                .andExpect(jsonPath("$.producto.name", is(producto.getName())));
        } */

/*         // Test. Producto no encontrado
        @Test
        @WithMockUser(username = "vrmachado@gmail.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}
        public void testProductoNoEncontrado() throws Exception {
                // given
                int productoId = 1;

                given(productoService.findById(productoId)).willReturn(null);

                // when

                ResultActions response = mockMvc.perform(get("/productos/{id}", productoId));

                // then

                response.andExpect(status().isNotFound());

        } */

        /* @DisplayName("Test asignar attendee a un evento con Admin ")
        @Test
        @WithMockUser(username = "Mapi@blue.com", authorities = { "ADMIN" }) 
        
        void testAttendeeRegistrar() throws Exception {

                // given
                Integer eventId = 1;

                Event eventB = Event.builder()
                .title("Language exchange")
                .target(Target.INTERNS)
                .description("Evento para conocer gente")
                .startDate(LocalDate.of(2024, 04, 10))
                .endDate(LocalDate.of(2024, 05, 10))
                .startTime(LocalTime.of(12, 10))
                .endTime(LocalTime.of(15, 30))
                .eventStatus(EventStatus.ENABLE)
                .mode(Mode.ONLINE)
                .place("Valencia")
                .build();

                Attendee attendee12 = Attendee.builder()
                .name("Maricarmen")
                .surname("Gomez")
                .globalId(27656)
                .mail("Maricarmen@blue.com")
                .profile(Profile.INTERNAL)
                .build();

        
                attendee12.setEvents(Set.of(eventB));

                String jsonStringAttendee = objectMapper.writeValueAsString(attendee12);
 


                 mockMvc.perform(MockMvcRequestBuilders
                 .post("/api/events/{id}/register", eventId)
                 .contentType("application/json")
                 .content(jsonStringAttendee))
                 .andDo(print())
                 .andExpect(status().isAccepted());
                 //.andExpect(jsonPath("$.[Attendee Persistido]", is(attendee03.getName())));
 
        } */
}
 