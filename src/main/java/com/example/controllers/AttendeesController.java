package com.example.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
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
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Attendee;
import com.example.entities.AttendeeDTO;
import com.example.entities.Event;
import com.example.entities.EventDTO;
import com.example.services.AttendeesService;
import com.example.services.EventsService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttendeesController {

    private final AttendeesService attendeesService;
    private final EventsService eventsService;

    /*
     * US 1.1. Manage a new attendee profile.
     * As Administrator I want to create an attendee profile to the event.
     */

    // 1.1.a) Administrator can create the attendee's profile.
    @PostMapping("/attendee")
    @Transactional
    public ResponseEntity<Map<String, Object>> createAttendee(@Valid @RequestBody Attendee attendee,
            BindingResult validationResults) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        if (validationResults.hasErrors()) {

            List<String> errors = new ArrayList<>();

            List<ObjectError> objectErrors = validationResults.getAllErrors();
            objectErrors.forEach(objectError -> errors.add(objectError.getDefaultMessage()));

            responseAsMap.put("errors", errors);
            responseAsMap.put("attendee", attendee);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;
        }

        try {

            Attendee attendeeSaved = attendeesService.save(attendee);
            String successMessage = "The attendee was successfully created";
            responseAsMap.put("successMessage", successMessage);
            responseAsMap.put("attendee Saved", attendeeSaved);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.CREATED);

        } catch (DataAccessException e) {
            String error = "Error when trying to save the event attendee and the most likely cause "
                    + e.getMostSpecificCause();

            responseAsMap.put("error", error);
            responseAsMap.put("Attendee", attendee);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    // 1.1.b) Administrator can modify the attendee's profile using Global ID
    @PutMapping("/attendee/{globalId}")
    public ResponseEntity<Map<String, Object>> updateAttendeeByIdGlobal(
            @Valid @RequestBody AttendeeDTO updatedAttendee,
            BindingResult validationResults,
            @PathVariable(name = "globalId", required = true) Integer idGlobal) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity;

        if (validationResults.hasErrors()) {
            List<String> errors = new ArrayList<>();
            validationResults.getAllErrors().forEach(objectError -> errors.add(objectError.getDefaultMessage()));

            responseAsMap.put("errors", errors);
            responseAsMap.put("attendee", updatedAttendee);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);
            return responseEntity;
        }

        try {
            Attendee existingAttendee = attendeesService.findByGlobalId(idGlobal);

            if (existingAttendee == null) {
                String errorMessage = "Attendee not found with globalId: " + idGlobal;
                responseAsMap.put("error", errorMessage);
                responseAsMap.put("attendee", updatedAttendee);

                responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.NOT_FOUND);
                return responseEntity;
            }

            existingAttendee.setName(updatedAttendee.getName());
            existingAttendee.setMail(updatedAttendee.getMail());
            existingAttendee.setSurname(updatedAttendee.getSurname());
            existingAttendee.setProfile(updatedAttendee.getProfile());

            Attendee attendeeUpdated = attendeesService.save(existingAttendee);

            String successMessage = "The attendee has been successfully modified";
            responseAsMap.put("successMessage", successMessage);
            responseAsMap.put("attendeeModified", attendeeUpdated);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);

        } catch (DataAccessException e) {
            String errorMessage = "Error when modifying the attendee's data. Cause: " + e.getMostSpecificCause();
            responseAsMap.put("error", errorMessage);
            responseAsMap.put("Attendee", updatedAttendee);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    // 1.1.c) Administrator can delete the attendee's profile using Global ID
    @DeleteMapping("/attendee/{globalId}")
    public ResponseEntity<Map<String, Object>> deleteAttendeeByIdGlobal(
            @PathVariable(name = "globalId", required = true) Integer idGlobal) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {
            attendeesService.deleteAttendeeByIdGlobal(idGlobal);
            String successMessage = "attendee with id: " + idGlobal + ", is removed";
            responseAsMap.put("successMessage", successMessage);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);

        } catch (DataAccessException e) {
            String error = "Error when trying to delete the attendee and the most likely cause is" +
                    e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    /*
     * US 1.5. Consult the list of attendees.
     * As an Administrator I would like to check all attendees for an event.
     */
    @GetMapping("event/{id}/attendees")
    public ResponseEntity<Map<String, Object>> getAllEventAttendees(
            @PathVariable(name = "id", required = true) Integer idEvent) {

        Map<String, Object> responseAsMap = new HashMap<>();

        try {
            List<Attendee> eventAttendees = attendeesService.findAllEventAttendeesById(idEvent);
            List<AttendeeDTO> attendeesDTOList = new ArrayList<>();

            if (!eventAttendees.isEmpty()) {
                for (Attendee attendee : eventAttendees) {
                    AttendeeDTO attendeeDto = new AttendeeDTO();
                    attendeeDto.setName(attendee.getName());
                    attendeeDto.setSurname(attendee.getSurname());
                    attendeeDto.setGlobalId(attendee.getGlobalId());
                    attendeeDto.setMail(attendee.getMail());

                    attendeesDTOList.add(attendeeDto);
                }

                String successMessage = "The list of event attendees has been successfully created";
                responseAsMap.put("availableEventAttendees", attendeesDTOList);
                responseAsMap.put("successMessage", successMessage);
                return new ResponseEntity<>(responseAsMap, HttpStatus.OK);
            } else {
                responseAsMap.put("availableEventAttendees", Collections.emptyList());
                responseAsMap.put("successMessage", "No attendees available for the specified event");
                return new ResponseEntity<>(responseAsMap, HttpStatus.OK);
            }

        } catch (Exception e) {
            responseAsMap.put("error", "An error occurred while processing the request. " + e.getMessage());
            return new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * US 2.1. Attendee consults available events.
     * As an Attendee I would like to check all available classes for future dates.
     * Available means enable status.
     */
    @GetMapping("/events/available/attendeee/{id}")
    public ResponseEntity<Map<String, Object>> consultAvailableEvents(@PathVariable(value = "id") Integer globalId) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {

            List<Event> listaEventos = eventsService.findAllEvents();

            for (Event event : listaEventos) {

                Attendee attendee = attendeesService.findByGlobalId(globalId);

                if (eventsService.availableEvents(event)) {

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
                    responseAsMap.put("Message", "There are no available events");
                    return new ResponseEntity<>(responseAsMap, HttpStatus.NOT_FOUND);
                }

            }

        } catch (Exception e) {
            responseAsMap.put("Message", "An error occurred while processing the request");
            return new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }
    
}
