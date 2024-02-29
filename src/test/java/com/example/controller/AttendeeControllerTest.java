// package com.example.controller;

// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.ArrayList;
// import java.util.List;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

// import static org.hamcrest.CoreMatchers.is;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.BDDMockito.given;
// import static org.mockito.BDDMockito.willDoNothing;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
// import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.mock.web.MockBodyContent;
// import org.springframework.mock.web.MockMultipartFile;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.ResultActions;
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

//     @Autowired
//     private MockMvc mockMvc; // Simular peticiones HTTP

//     @MockBean
//     private AttendeesService attendeesService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Autowired
//     private WebApplicationContext context;

//     @BeforeEach
//     public void setUp() {
//         mockMvc = MockMvcBuilders
//                 .webAppContextSetup(context)
//                 .apply(springSecurity())
//                 .build();
//     }

//     // @Test
//     //     @DisplayName("Test try to save an attendee without authorization")
//     //     void testSaveAttendee() throws Exception {
//     //              Attendee attendee = Attendee.builder()
//     //             .id(1)
//     //             .name("Mariana")
//     //             .surname("Urbina")
//     //             .globalId(43166546)
//     //             .mail("mariu@blue.com")
//     //             .options(Options.BOOTCAMPER)
//     //             //.events(event1)
//     //             .build();

//     //         // attendee.addEvent(event1);
//     //         attendeesService.save(attendee);

//     //             String jsonStringAttendee = objectMapper.writeValueAsString(attendee);

//                 // //El codigo siguiente es para cuando se reciben attendee e imagen en el cuerpo de la peticion por separado


//                 // MockBodyContent bytesArrayProduct = new MockBodyContent("attendee",jsonStringAttendee.getBytes());

//                 // // multipart: perfoms a POST request
//                 // mockMvc.perform(multipart("/attendees")
//                 //                 .file("file", null)
//                 //                 .file(bytesArrayProduct))
//                 //                 .andDo(print())
//                 //                 .andExpect(status().isUnauthorized());

//                      /*
//                     * El siguiente codigo es: PARA CUANDO TODO EL PRODUCTO VIENE EN EL CUERPO DE LA PETICION, SIN IMAGEN
//                     */
// //REVISAR
//                                 //  mockMvc.perform(post(urlTemplate:"/attendees")
//                                 //  .contentType("application/json")
//                                 //  .contentType(jsonStringAttendee)).andDo(print())
//                                 //  .andExpect(status().isForbidden());

//        // }


//         @DisplayName("Test guardar asistente con usuario mockeado")
//         @Test
//         @WithMockUser(username = "vrmachado@gmail.com", 
//         authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}
//         void testGuardarProductoConUserMocked() throws Exception {

//                 // given

//                 Event event2 = Event.builder()
//                 .description(null)
//                 .title("docena")
//                 .build();
                
//                 Attendee attendee = Attendee.builder()
//                             .name("Mariana")
//                             .surname("Urbina")
//                             .globalId(43166546)
//                             .mail("mariu@blue.com")
//                             .options(Options.BOOTCAMPER)
//                             //.events(event2)
//                             .build();

//                             // event2.addAttendees(attendee);

//                  given(attendeesService.save(any(Attendee.class)))
//                  .willAnswer(invocation -> invocation.getArgument(0));

//                 // when
//                 String jsonStringAttendee = objectMapper.writeValueAsString(attendee);

//                 // MockMultipartFile bytesArrayAttendee = new MockMultipartFile("attendee",
//                 //                 null, "application/json", jsonStringAttendee.getBytes());

//                 ResultActions response = mockMvc.perform(multipart("/attendees")
//                                 .file("file", null)
//                                 .file(bytesArrayAttendee));
//                 // then

//                 response.andDo(print())
//                                 .andExpect(status().isCreated())
//                                 .andExpect(jsonPath("$.attendee.name", is(attendee.getName())))
//                                 .andExpect(jsonPath("$.attendee.globalId", is(attendee.getGlobalId())));
//         }

//         @WithMockUser(username = "vrmachado@gmail.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}
//         public void testListarProductos() throws Exception {

//                 // given

//                 List<Attendee> attendees = new ArrayList<>();

//                 Event event = Event.builder()
//                                 .description(null)
//                                 .name("docena")
//                                 .build();

//                 Attendee attendee = Attendee.builder()
//                                 .name("Mariana")
//                                 .surname("Urbina")
//                                 .globalId(43166546)
//                                 .mail("mariu@blue.com")
//                                 .options(Options.BOOTCAMPER)
//                                 .events(event)
//                                 .imagenProducto("perro.jpeg")
//                                 .build();

//                 Event event1 = Event.builder()
//                                 .description(null)
//                                 .name("unidad")
//                                 .build();

//                 Attendee attendee1 = Attendee.builder()
//                                 .name("Mariana")
//                                 .surname("Urbina")
//                                 .globalId(43166546)
//                                 .mail("mariu@blue.com")
//                                 .options(Options.BOOTCAMPER)
//                                 .events(event1)
//                                 .imagenProducto("perro.jpeg")
//                                 .build();

//                 attendees.add(attendee);
//                 attendees.add(attendee1);

//                 given(attendeesService.findAll(Sort.by("name")))
//                                 .willReturn(attendees);

//                 // when

//                 ResultActions response = mockMvc.perform(get("/attendees"));

//                 // then

//                 response.andExpect(status().isOk())
//                                 .andDo(print())
//                                 .andExpect(jsonPath("$.size()", is(attendees.size())));

//         }

// }
