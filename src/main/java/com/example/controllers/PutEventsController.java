package com.example.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Event;
import com.example.entities.Event;
import com.example.services.EventsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PutEventsController {

        private final EventsService eventsService;

    @PutMapping("/event/{id}")
    public ResponseEntity<Map<String, Object>> updateEvent(@RequestBody Event event,
            @PathVariable(name = "id") Integer id) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity;

        Optional<Event> incomingEvent = eventsService.findById(id);
        if (incomingEvent == null) {
            String errorMessage = "Event with Id " + id + " not found";
            responseAsMap.put("errorMessage", errorMessage);
            return new ResponseEntity<>(responseAsMap, HttpStatus.NOT_FOUND);
        }


        // This checks if the incoming target is the same as the original target
        // if it's not equal, an error message appears
/*         Integer existingEventTarget = event.getTarget().ge;
        if (!globalIdEvent.equals(existingEventGlobalId)) {
            String errorMessage = "Modification of globalId is not allowed";
            responseAsMap.put("errorMessage", errorMessage);
            return new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);
        }
 */
        // Actualizar el Event
      /*   try {
            // Actualizar solo los campos no nulos del objeto Event
            if (event.getFirstName() != null) {
                existingEvent.setFirstName(event.getFirstName());
            }
            if (event.getSurname() != null) {
                existingEvent.setSurname(event.getSurname());
            }
            if (event.getEmails() != null) {
                existingEvent.setEmails(event.getEmails());
            }
            if (event.getProfile() != null) {
                existingEvent.setProfile(event.getProfile());
            }
            if (event.getInitialLevel() != null) {
                existingEvent.setInitialLevel(event.getInitialLevel());
            }
            if (event.getStatus() != null) {
                existingEvent.setStatus(event.getStatus());
            }

            Event eventUpdate = eventService.save(existingEvent);
            String successMessage = "The event has been saved successfully";
            responseAsMap.put("successMessage", successMessage);
            responseAsMap.put("Event update", eventUpdate);
            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);
        } catch (DataAccessException e) {
            String error = "Error updating the event: " + e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseAsMap.put("The event has attempted to update", event);
            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        } */

        return null; 
    } 
 
}
