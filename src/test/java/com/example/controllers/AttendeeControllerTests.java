package com.example.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

import com.example.entities.Event;
import com.example.entities.EventStatus;
import com.example.entities.Mode;
import com.example.entities.Profile;
import com.example.entities.Target;
import com.example.entities.Attendee;
import com.example.services.AttendeesService;
// import com.example.helpers.FileDownloadUtil;
// import com.example.helpers.FileUploadUtil;
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

        @Autowired
        private ObjectMapper objectMapper;

        // @MockBean
        // private FileUploadUtil fileUploadUtil;

        // @MockBean
        // private FileDownloadUtil fileDownloadUtil;

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
        @DisplayName("Test de intento de guardar un attendee sin autorizacion")
        void testGuardarPAttendee() throws Exception {
               Attendee attendee01 = Attendee.builder()
                .name("Maricarmen")
                .surname("Gomez")
                .globalId(2765)
                .mail("Maricarmen@blue.com")
                .profile(Profile.INTERNAL)
                .build();

        Attendee attendee02 = Attendee.builder()
                .name("Maricarmen")
                .surname("Gomez")
                .globalId(2765)
                .mail("Maricarmen@blue.com")
                .profile(Profile.INTERNAL)
                .build();

        Event event0 = Event.builder()
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

        event0.setAttendees(Set.of(attendee01,attendee02));

               String jsonStringAttendee = objectMapper.writeValueAsString(attendee01);

                MockMultipartFile bytesArrayProduct = new MockMultipartFile("producto",
                                null, "application/json", jsonStringAttendee.getBytes());

                // multipart: perfoms a POST request
                mockMvc.perform(multipart("/productos")
                                .file("file", null)
                                .file(bytesArrayProduct))
                                .andDo(print())
                                .andExpect(status().isUnauthorized());

        }

        @DisplayName("Test guardar producto con usuario mockeado")
        @Test
        @WithMockUser(username = "gabybcr1542@blue.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}
        void testGuardarAttendeeConUserMocked() throws Exception {
                // given
                Event event1 = Event.builder()
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

                Attendee attendee03 = Attendee.builder()
                .name("Maricarmen")
                .surname("Gomez")
                .globalId(2765)
                .mail("Maricarmen@blue.com")
                .profile(Profile.INTERNAL)
                .build();

                given(attendeesService.save(any(Attendee.class)))
                                .willAnswer(invocation -> invocation.getArgument(0));
                // getArgument(0) devuelve el primer elemento del objeto Attendee creado.
                

                // when
                String jsonStringAttendee = objectMapper.writeValueAsString(attendee03);

                MockMultipartFile bytesArrayAttendee = new MockMultipartFile("attendee",
                                null, "application/json", jsonStringAttendee.getBytes());

                ResultActions response = mockMvc.perform(multipart("/attendees")
                                .file("file", null)
                                .file(bytesArrayAttendee));
                // then

                response.andDo(print())
                                .andExpect(status().isCreated());
  //                              .andExpect(jsonPath("$.[Producto Persistido]", is(producto.getName())))
  //                              .andExpect(jsonPath("$.[Producto Persistido].description", is(producto.getDescription())));
        }
/*         @DisplayName("Test de enlistar producto con usuario mockeado")
        @Test
        @WithMockUser(username = "vrmachado@gmail.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}
        public void testListarProductos() throws Exception {

                // given

                List<Producto> productos = new ArrayList<>();

                Presentacion presentacion = Presentacion.builder()
                                .description(null)
                                .name("docena")
                                .build();

                Producto producto = Producto.builder()
                                .name("Camara")
                                .description("Resolucion Alta")
                                .price(2000.00)
                                .stock(40)
                                .presentacion(presentacion)
                                .imagen("perro.jpeg")
                                .build();

                Presentacion presentacion1 = Presentacion.builder()
                                .description(null)
                                .name("unidad")
                                .build();

                Producto producto1 = Producto.builder()
                                .name("Pixel 7")
                                .description("Google Pixel Phone")
                                .price(2000.00)
                                .stock(40)
                                .presentacion(presentacion1)
                                .imagen("perro.jpeg")
                                .build();

                productos.add(producto);
                productos.add(producto1);

                given(productoService.findAll(Sort.by("name")))
                                .willReturn(productos);

                // when

                ResultActions response = mockMvc.perform(get("/productos"));

                // then

                response.andExpect(status().isOk())
                                .andDo(print())
                                .andExpect(jsonPath("$.size()", is(productos.size())));

        }
        @DisplayName("Test Recuperar un producto por el id")
        @Test
        @WithMockUser(username = "vrmachado@gmail.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}
        public void testRecuperarProductoPorId() throws Exception {
                // given
                int productoId = 1;

                Presentacion presentacion = Presentacion.builder()
                                .description(null)
                                .name("docena")
                                .build();

                Producto producto = Producto.builder()
                                .name("Camara")
                                .description("Resolucion Alta")
                                .price(2000.00)
                                .stock(40)
                                .presentacion(presentacion)
                                .imagen("perro.jpeg")
                                .build();

                given(productoService.findById(productoId)).willReturn(producto);

                // when

                ResultActions response = mockMvc.perform(get("/productos/{id}", productoId));

                // then

                response.andExpect(status().isOk())
                                .andDo(print())
                                .andExpect(jsonPath("$.producto.name", is(producto.getName())));
        }

        // Test. Producto no encontrado
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

        }

        //  Test. Actualizar un producto
        @Test
        @WithMockUser(username = "vrmachado@gmail.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}
        public void testActualizarProducto() throws Exception {

                // given

                int productoId = 1;

                Presentacion presentacionGuardada = Presentacion.builder()
                                .description(null)
                                .name("docena")
                                .build();

                Producto productoGuardado = Producto.builder()
                                .name("Camara")
                                .description("Resolucion Alta")
                                .price(2000.00)
                                .stock(40)
                                .presentacion(presentacionGuardada)
                                .imagen("perro.jpeg")
                                .build();

                Presentacion presentacionActualizada = Presentacion.builder()
                                .description(null)
                                .name("unidad")
                                .build();

                Producto productoActualizado = Producto.builder()
                                .name("HDCamara")
                                .description("Muy Alta Resolucion")
                                .price(2500.00)
                                .stock(400)
                                .presentacion(presentacionActualizada)
                                .imagen("perro.jpeg")
                                .build();

                given(productoService.findById(productoId)).willReturn(productoGuardado)
                                .willReturn(productoGuardado);
                given(productoService.save(any(Producto.class)))
                                .willAnswer(invocation -> invocation.getArgument(0));

                // when

                // Si todo el producto se recibe en el cuerpo de la peticion procedemos
                // de la forma siguiente, de lo contrario, si por una parte va el producto
                // y por otra la imagen, hay que proceder de manera diferente (muy similar
                // al test de persistir un producto con su imagen)

                ResultActions response = mockMvc.perform(put("/productos/{id}", productoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(productoActualizado)));

                // then

                response.andExpect(status().isOk());
   //                             .andDo(print())
  //                              .andExpect(jsonPath("$.[Producto Actualizado]", is(productoActualizado.getName())));
        }

        // Test. Eliminar un producto
        @Test
        @WithMockUser(username = "vrmachado@gmail.com", authorities = { "ADMIN" }) // puede ser {"ADMIN", "USER"}
        public void testEliminarProducto() throws Exception {

                // given

                int productoId = 1;

                Presentacion presentacion = Presentacion.builder()
                                .description(null)
                                .name("docena")
                                .build();

                Producto producto = Producto.builder()
                                .name("Camara")
                                .description("Resolucion Alta")
                                .price(2000.00)
                                .stock(40)
                                .presentacion(presentacion)
                                .imagen("perro.jpeg")
                                .build();

                given(productoService.findById(productoId))
                                .willReturn(producto);

                willDoNothing().given(productoService).delete(producto);

                // when

                ResultActions response = mockMvc.perform(delete("/productos/{id}", productoId));

                // then

                response.andExpect(status().isOk())
                                .andDo(print());

        }
*/
}
 