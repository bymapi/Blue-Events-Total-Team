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


}
