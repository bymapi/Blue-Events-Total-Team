/* package com.example.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.AttendeeDTO;
import com.example.entities.Event;
import com.example.entities.EventDTO;
import com.example.services.EventsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConsultAvailableEventsController {

    private final EventsService eventsService;

    // US 2.1. Attendee consults available events
    // As an Attendee I would like to check all available classes for future dates.
    // Available means enable status.

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
 */