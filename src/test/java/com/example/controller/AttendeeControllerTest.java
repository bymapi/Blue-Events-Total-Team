// package com.example.controller;


// import java.time.LocalDate;
// import java.time.LocalTime;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
// import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.mock.web.MockBodyContent;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.springframework.web.context.WebApplicationContext;

// import com.example.entities.Attendee;
// import com.example.entities.Event;
// import com.example.entities.Mode;
// import com.example.entities.Options;
// import com.example.entities.Target;
// import com.example.services.AttendeesService;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import jakarta.transaction.Transactional;
// @Transactional
// @SpringBootTest
// @AutoConfigureMockMvc // Test a los controladores, a los end points, teniendo Spring Securiy
//                       // configurado
// // @ContextConfiguration(classes = SecurityConfig.class)
// // @WebAppConfiguration
// @AutoConfigureTestDatabase(replace = Replace.NONE)
// // @WithMockUser(username = "vrmachado@gmail.com",
// // authorities = {"ADMIN", "USER"})
// // @WithMockUser(roles="ADMIN") - Error 403

// public class AttendeeControllerTest {

//  @Autowired
//         private MockMvc mockMvc; // Simular peticiones HTTP

        
//         @MockBean
//         private AttendeesService attendeesService;

//         @Autowired
//         private ObjectMapper objectMapper;

      

//         @Autowired
//         private WebApplicationContext context;

//         @BeforeEach
//         public void setUp() {
//                 mockMvc = MockMvcBuilders
//                                 .webAppContextSetup(context)
//                                 .apply(springSecurity())
//                                 .build();
//         }



//         @Test
//         @DisplayName("Test de intento de guardar un producto sin autorizacion")
//         void testGuardarProducto() throws Exception {
//                 Event event1 = Event.builder()
//             .title("French for non-native speakers")
//             .target(Target.INTERNS)
//             .description("French classes, level B2")
//             .startDate(LocalDate.of(2024, 03, 10))
//             .endDate(LocalDate.of(2024, 03, 10))
//             .startTime(LocalTime.of(10, 30))
//             .endTime(LocalTime.of(12, 30))
//             .mode(Mode.ONLINE)
//             .place("Blue offices, Valence")
//             .build();

//        Attendee attendee = Attendee.builder()
//                 .id(1)
//                 .name("Mariana")
//                 .surname("Urbina")
//                 .globalId(43166546)
//                 .mail("mariu@blue.com")
//                 .options(Options.BOOTCAMPER)
//                 //.events(event1)
//                 .build();

//                 String jsonStringAttendee = objectMapper.writeValueAsString(attendee);

//                 MockBodyContent bytesArrayProduct = new MockBodyContent("attendee",jsonStringAttendee.getBytes());

//                 // multipart: perfoms a POST request
//                 mockMvc.perform(multipart("/attendees")
//                                 .file("file", null)
//                                 .file(bytesArrayProduct))
//                                 .andDo(print())
//                                 .andExpect(status().isUnauthorized());

//         }
        
// }
