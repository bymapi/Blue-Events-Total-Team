/* /**
 * Segun el enfoque: Una prueba unitaria se divide en tres partes
 *
 * 1. Arrange: Setting up the data that is required for this test case
 * 2. Act: Calling a method or Unit that is being tested.
 * 3. Assert: Verify that the expected result is right or wrong.
 *
 * Segun el enfoque BDD (Behaviour Driven Development). 'Given-When-Then' como lenguaje comun con BDD
* 
* Para definir los casos BDD para una historia de usuario se deben definir bajo el patrón "Given-When-Then"
* , que se define como sigue:
 *
 * 1. given (dado) : Se especifica el escenario, las precondiciones.
 * 2. when (cuando) : Las condiciones de las acciones que se van a ejecutar.
 * 3. then (entonces) : El resultado esperado, las validaciones a realizar.
*
* Un ejemplo practico seria:
*
* Given: Dado que el usuario no ha introducido ningun dato en el formulario.
* When: Cuando se hace click en el boton de enviar.
* Then: Se deben de mostrar los mensajes de validación apropiados.
*
* "Role-Feature-Reason" como lenguaje común con BDD
*
* Este patrón se utiliza en BDD para ayudar a la creación de historias de usuarios. Este se define como:
*
* As a "Como" : Se especifica el tipo de usuario.
* I want "Deseo" : Las necesidades que tiene.
* So that "Para que" : Las caracteristicas para cumplir el objetivo.
*
* Un ejemplo práctico de historia de usuario sería: Como cliente interesado, deseo ponerme en contacto mediante formulario, 
* para que atiendan mis necesidades. 
*
* Parece que BDD y TDD son la misma cosa, pero la principal diferencia entre ambas esta en el alcance. TDD es una practica de desarrollo 
* (se enfoca en como escribir el codigo y como deberia trabajar ese codigo) mientras que BDD es una metodologia de equipo (Se enfoca
* en porque debes escribir ese codigo y como se deberia de comportar ese codigo)
*
* En TDD el desarrollador escribe los tests mientras que en BDD el usuario final (o PO o analista) en conjunto con los testers escriben
* los tests (y los Devs solo generan el codigo necesario para ejecutar dichos tests)
*
* Tambien existe ATDD (Acceptance Test Driven Development), que es mas cercana a BDD ya que no es una practica,
* sino una metodologia de trabajo, pero la diferencia esta nuevamente en el alcance, a diferencia de BDD, ATDD se extiende aun 
* mas en profundizar la búsqueda de que lo que se esta haciendo no solo se hace de forma correcta, sino que tambien 
* es lo correcto a hacer.
*
 */
package com.example.repository;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.security.entities.OurUser;
import com.example.security.entities.Role;
import com.example.security.repository.OurUserRepository;

import lombok.RequiredArgsConstructor;



@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@RequiredArgsConstructor
public class UserRepositoryTests {
   
    @Autowired
    private OurUserRepository ourUserRepository;

 private OurUser ourUser0;

    @BeforeEach
    void setUp(){
         ourUser0 = OurUser.builder()
        .email("Alberta@gmail.com")
        .password("65768543")
        .role(Role.USER).build();
    }


    // Test para agregar un USER (from security)
    @DisplayName("TEST TO ADD AN USER")
    @SuppressWarnings("null")
    @Test
    
    public void testAddOurUser() {

        //Given: Dado que tenemos un ususario, inyectaremos en este momento, 

        OurUser ourUser = OurUser.builder()
        .email("Francesco@gmail.com")
        .password("6576874")
        .role(Role.ADMIN).build();

        //When:
       // Cuando este usuario se persista sera cuando yo llame 
       // a repositorio y llame a la entidad OurUser

        OurUser ourUserSaved = ourUserRepository.save(ourUser);

        //Then:
        
        assertThat(ourUserSaved).isNotNull();
        assertThat(ourUserSaved.getId()).isGreaterThan(0);
    }

    // Test para obtener una lista de USER (from security)
    @Test
    @DisplayName("TEST TO RECEIVE A LIST OF USERS")
    public void testFindAllUsers(){

        //Given: Dado que tenemos un ususario, inyectaremos en este momento, 

        OurUser ourUser1 = OurUser.builder()
        .email("Juliana@blue.com")
        .password("6592574")
        .role(Role.ADMIN)
        .build();

        ourUserRepository.save(ourUser0);
        ourUserRepository.save(ourUser1);

        //When:

        var users = ourUserRepository.findAll();

        //Then:

        assertThat(users).isNotNull();
        assertThat(users).size().isEqualTo(2);
    }
    

    //Test to recover a USER by its ID
    @Test
    @DisplayName("TEST TO RECEIVE A LIST OF USERS by ID")
    public void testFindById (){

        //Given:

        ourUserRepository.save(ourUser0);

        
        //When:

        OurUser foundUser = ourUserRepository.findById(ourUser0.getId()).get();

        //Then:
        //Ask if the user found is not equal to 0 

        assertThat(foundUser.getId()).isNotEqualTo(0);

    }

    @Test
    @DisplayName("TEST FOR UPDATING AN USER CORRECTLY")
    public void testUpdateUser(){

        //Given:

        ourUserRepository.save(ourUser0);


        //When:

        OurUser userUpdated = ourUserRepository.findByEmail(ourUser0.getEmail()).get();
        userUpdated.setEmail("new@blue.com");
        userUpdated.setPassword("new76532");
        userUpdated.setRole(Role.USER);

        OurUser updatedUser = ourUserRepository.save(userUpdated);

        //Then:

        assertThat(updatedUser.getEmail()).isEqualTo("new@blue.com");
    }

    @DisplayName("TEST TO DELETE AN USER")
    @Test
    public void testDeleteUser() {

        // given
        ourUserRepository.save(ourUser0);

        // when
        ourUserRepository.delete(ourUser0);
        Optional<OurUser> optionalUser = ourUserRepository.findByEmail(ourUser0.getEmail());

        // then
        assertThat(optionalUser).isEmpty();
    }
        

}
 