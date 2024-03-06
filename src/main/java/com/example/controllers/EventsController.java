package com.example.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
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
import com.example.entities.EventDTOAdmin;
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

    /*
     * US 1.2. Create a new internal event.
     * As Administrator I want to create new internal events.
     */
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

    /*
     * US 1.3. Modify an event.
     * As Administrator I want to modify or delete an event.
     */
    // 1.3.a) Administrator can modify events by its id
    @PutMapping("/events/{id}")
    public ResponseEntity<Map<String, Object>> updateEvent(@Valid @RequestBody Event event,
            BindingResult validationResults,
            @PathVariable(name = "id", required = true) Integer idEvent) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        // Check if there has been errors while creating the event
        if (validationResults.hasErrors()) {

            List<String> errors = new ArrayList<>();

            List<ObjectError> objectErrors = validationResults.getAllErrors();

            objectErrors.forEach(objectError -> errors.add(objectError.getDefaultMessage()));

            responseAsMap.put("errors", errors);
            responseAsMap.put("Malformed event", event);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }

        try {
            event.setId(idEvent);
            var originalTarget = event.getTarget();
            Event eventUpdated = eventsService.eventSaved(event);
            if (!eventUpdated.getTarget().equals(originalTarget)) {

                String error = "ERROR";

                responseAsMap.put("errors", error);

                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

                return responseEntity;

            } else {

                Event eventUpdated2 = eventsService.eventSaved(eventUpdated);

                String successMessage = "Event was succesfully updated";
                responseAsMap.put("Success Message", successMessage);
                responseAsMap.put("Updated event", eventUpdated);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
            }

        } catch (DataAccessException e) {
            String error = "Error while updating the event and the most specific cause is: "
                    + e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseAsMap.put("Event intended to update", event);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    // 1.3.b) Administrator can delete events by its id
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Map<String, Object>> deleteEventById(
            @PathVariable(name = "globalId", required = true) Integer eventId) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {
            eventsService.deleteEventById(eventId);
            String successMessage = "event with id: " + eventId + ", is removed";
            responseAsMap.put("successMessage", successMessage);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);

        } catch (DataAccessException e) {
            String error = "Error when trying to delete the event and the most likely cause" +
                    e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    /*
     * US 1.4. Consult available events.
     * As an Administrator I would like to check all available events for future
     * dates
     */
    // As an Administrator can list all available events for future dates in any
    // state (enable/disable).
    @GetMapping("/events")
    public ResponseEntity<Map<String, Object>> findAllAvailableEvents(@RequestParam(required = false) String title) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {

            List<Event> allEvents = eventsService.findAllEvents();
            List<EventDTOAdmin> listEventDto = new ArrayList<>();

            for (var event : allEvents) {
                if (event.getStartDate().isAfter(LocalDate.now()) ||
                        (event.getStartDate().isEqual(LocalDate.now())
                                && event.getStartTime().isBefore(LocalTime.now()))) {
                    EventDTOAdmin eventDTOAdmin = new EventDTOAdmin();

                    eventDTOAdmin.setTitle(event.getTitle());
                    eventDTOAdmin.setDescription(event.getDescription());
                    eventDTOAdmin.setStartDate(event.getStartDate());
                    eventDTOAdmin.setEndDate(event.getEndDate());
                    eventDTOAdmin.setStartTime(event.getStartTime());
                    eventDTOAdmin.setEndTime(event.getEndTime());
                    eventDTOAdmin.setMode(event.getMode());
                    eventDTOAdmin.setPlace(event.getPlace());
                    eventDTOAdmin.setEventStatus(event.getEventStatus());

                    listEventDto.add(eventDTOAdmin);

                    String successMessage = "the list of available events well created";

                    responseAsMap.put("available Events", listEventDto);
                    responseAsMap.put("successMessage", successMessage);

                    responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);

                }

                if (!allEvents.isEmpty()) {

                    String successMessage = "The list of available events has been successfully created";
                    responseAsMap.put("availableEvents", listEventDto);
                    responseAsMap.put("successMessage", successMessage);
                    return new ResponseEntity<>(responseAsMap, HttpStatus.OK);

                } else {

                    responseAsMap.put("availableEvents", Collections.emptyMap());
                    responseAsMap.put("successMessage", "No events available ");

                    responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);
                }
            }

        } catch (DataAccessException e) {
            String error = "Error when trying to display your event list and the most likely cause" +
                    e.getMostSpecificCause();
            responseAsMap.put("Error", error);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    
}
