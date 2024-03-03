package com.example.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Attendee;
import com.example.entities.Event;
import com.example.entities.EventDTO;
import com.example.entities.Target;
import com.example.exception.ResourceNotFoundException;
import com.example.services.AttendeesService;
import com.example.services.EventsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventsController {

    private final EventsService eventsService;

    // Método enabler para comprobar que devuelve todos los eventos existentes:

    @GetMapping("/events")
    public ResponseEntity<List<Event>> findAll(@RequestParam(required = false) String title) {

        List<Event> events = new ArrayList<>();

        if (title == null) {

            eventsService.findAllEvents().forEach(events::add);

        } else
            eventsService.findEventsByTitleContaining(title).forEach(events::add);

        if (events.isEmpty()) {

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // US 1.2. Create a new internal event
    // it does also validate if it has been created properly

    @PostMapping("/events")
    public ResponseEntity<Map<String, Object>> createEvent(@Valid @RequestBody Event event,
            BindingResult validationResults) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        // First, we check if the event itself has errors

        if (validationResults.hasErrors()) {

            List<String> errors = new ArrayList<>();
            List<ObjectError> objectErrors = validationResults.getAllErrors();

            objectErrors.forEach(objectError -> errors.add(objectError.getDefaultMessage()));

            responseAsMap.put("errors", errors);
            responseAsMap.put("malformed event", event);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }

        // As long as the validation goes fine, we can proceed to create our super event

        try {

            Event eventCreated = eventsService.eventSaved(event);
            String successMessage = "The event was succesfully created";
            responseAsMap.put("Success Message", successMessage);
            responseAsMap.put("Created event", eventCreated);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.CREATED);

        } catch (DataAccessException e) {
            String error = "Something went wrong while creating the event and the most specific cause is: "
                    + e.getMostSpecificCause();

            responseAsMap.put("error", error);
            responseAsMap.put("event that was intended to be created", event);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Enjoy !
        return responseEntity;
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@Valid @RequestBody Event event,
            BindingResult validationResults,
            @PathVariable(name = "id", required = true) Integer idEvent) {
 
        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
 
        // Check if there has been errors while creating the event
        if (validationResults.hasErrors()) {
 
            List<String> errores = new ArrayList<>();
 
            List<ObjectError> objectErrors = validationResults.getAllErrors();
 
            objectErrors.forEach(objectError -> errores.add(objectError.getDefaultMessage()));
 
            responseAsMap.put("errores", errores);
            responseAsMap.put("Malformed event", event);
 
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);
 
            return responseEntity;
 
        }

        try {
            event.setId(idEvent);
            var targetAnterior=event.getTarget();
            Event eventActualizado = eventsService.eventSaved(event);
            if(!eventActualizado.getTarget().equals(targetAnterior)){
 
             String error="ERROR";
 
 
            responseAsMap.put("errores", error);
            
 
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);
 
            return responseEntity;
 
            } else {

                Event eventActualizado2 = eventsService.eventSaved(eventActualizado);
 
            String successMessage = "Event was succesfully updated";
            responseAsMap.put("Success Message", successMessage);
            responseAsMap.put("Updated event", eventActualizado);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
            }
        
 
 
        // try {
        //     event.setId(idEvent);
        //     Event eventActualizado = eventsService.eventSaved(event);


        //     Target existingEventTarget = event.getTarget();
        //     if (!eventActualizado.getTarget().equals(existingEventTarget)) {

        //     String errorMessage = "Modification of Target is not allowed";
        //     responseAsMap.put("errorMessage", errorMessage);
        //     return new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);
                
        //     }

        //     String successMessage = "Event was succesfully updated";
        //     responseAsMap.put("Success Message", successMessage);
        //     responseAsMap.put("Updated event", eventActualizado);
        //     responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
        } catch (DataAccessException e) {
            String error = "Error while updating the event and the most specific cause is: "
                    + e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseAsMap.put("Event intended to update", event);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
 
        return responseEntity;
 
    }

    @GetMapping("/events/available")
    public ResponseEntity<Map<String, Object>> consultAvailableEvents(@Valid @RequestBody Event event,
            BindingResult validationResults) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        if (validationResults.hasErrors()) {

            List<String> errores = new ArrayList<>();

            List<ObjectError> objectErrors = validationResults.getAllErrors();

            objectErrors.forEach(objectError -> errores.add(objectError.getDefaultMessage()));

            responseAsMap.put("errors", errores);
            responseAsMap.put("event", event);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;
        }

        try {
            
            List<Event> availableEvents = new ArrayList<>();

            if (eventsService.availableEvents(event)) {

                eventsService.findAllEvents().forEach(availableEvents::add);

                EventDTO eventDTO = new EventDTO();
                eventDTO.setTitle(event.getTitle());
                eventDTO.setDescription(event.getDescription());
                eventDTO.setStartDate(event.getStartDate());
                eventDTO.setEndDate(event.getEndDate());
                eventDTO.setStartTime(event.getStartTime());
                eventDTO.setEndTime(event.getEndTime());
                eventDTO.setMode(event.getMode());
                eventDTO.setPlace(event.getPlace());

                String successMessage = "There are available events";

                responseAsMap.put("EventResponse", eventDTO);
                responseAsMap.put("successMessage", successMessage);

                return new ResponseEntity<>(responseAsMap, HttpStatus.OK);
            } else {
                responseAsMap.put("Message", "Event not found");
                return new ResponseEntity<>(responseAsMap, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            responseAsMap.put("Message", "An error occurred while processing the request");
            return new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    


   
}


