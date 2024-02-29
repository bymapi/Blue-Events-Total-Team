package com.example.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // US 1.1. create the attendee's profile(persistir)
    @PostMapping("/attendee")
    @Transactional
    public ResponseEntity<Map<String, Object>> saveAttendees(@Valid @RequestBody Attendee attendee,
            BindingResult validationResults) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        if (validationResults.hasErrors()) {

            List<String> errores = new ArrayList<>();

            List<ObjectError> objectErrors = validationResults.getAllErrors();
            objectErrors.forEach(objectError -> errores.add(objectError.getDefaultMessage()));

            responseAsMap.put("errores", errores);
            responseAsMap.put("attendee", attendee);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;
        }

        try {

            Attendee attendeeSaved = attendeesService.save(attendee);
            String successMessage = "The attendee has persisted well";
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

    // 1.1. - Administrator can modify an attendee profile using Global ID.

    @PutMapping("/attendee/{globalId}")
    public ResponseEntity<Map<String, Object>> updateAttendee(@Valid @RequestBody Attendee attendee,
            BindingResult validationResults,
            @PathVariable(name = "globalId", required = true) Integer idGlobal) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        if (validationResults.hasErrors()) {

            List<String> errores = new ArrayList<>();

            List<ObjectError> objectErrors = validationResults.getAllErrors();

            objectErrors.forEach(objectError -> errores.add(objectError.getDefaultMessage()));

            responseAsMap.put("errores", errores);
            responseAsMap.put("attendee", attendee);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;
        }

        try {

            // Attendee attendeeUpdated =
            // attendeesService.updateAttendeeByGlobalId(idGlobal);
            attendee.setGlobalId(idGlobal);
            String successMessage = "The attendee has been well modified";
            responseAsMap.put("successMessage", successMessage);
            responseAsMap.put("attendee modified", attendee);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.CREATED);

        } catch (DataAccessException e) {
            String error = "Error when modifying the attendee's data and the most probable cause"
                    + e.getMostSpecificCause();

            responseAsMap.put("error", error);
            responseAsMap.put("Attendee", attendee);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    // 1.1. - Delete by Global Id

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
            String error = "Error when trying to delete the attendee and the most likely cause" +
                    e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    // Enabler: Get List of all Attendees
    @GetMapping("/attendees")

    public ResponseEntity<List<Attendee>> findAllStudentsList() {

        List<Attendee> attendees = attendeesService.findAllAttendees();
        return new ResponseEntity<>(attendees, HttpStatus.OK);
    }

    // 2.3. - Retrieve all events from a attendee:
    @GetMapping("/attendee/{globalId}/events")

    public ResponseEntity<Map<String, Object>> getAllEventsByAttendeeglobalId(
            @PathVariable(name = "globalId", required = true) Integer idGlobal) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {

            List<Event> allAttendeeEvents = eventsService.findEventsByAttendeeGlobalId(idGlobal);

            if (!allAttendeeEvents.isEmpty()) {
                for (var attendeeEvent : allAttendeeEvents) {
                    if (attendeeEvent.getStartDate().isAfter(LocalDate.now()) ||
                            (attendeeEvent.getStartDate().isEqual(LocalDate.now())
                                    && attendeeEvent.getStartTime().isBefore(LocalTime.now()))) {
                        EventDTO eventDTO = new EventDTO();
                        eventDTO.setTitle(attendeeEvent.getTitle());
                        eventDTO.setDescription(attendeeEvent.getDescription());
                        eventDTO.setStartDate(attendeeEvent.getStartDate());
                        eventDTO.setEndDate(attendeeEvent.getEndDate());
                        eventDTO.setStartTime(attendeeEvent.getStartTime());
                        eventDTO.setEndTime(attendeeEvent.getEndTime());
                        eventDTO.setMode(attendeeEvent.getMode());
                        eventDTO.setPlace(attendeeEvent.getPlace());

                        String successMessage = "the list of available events well created";

                        responseAsMap.put("available Events", eventDTO);
                        responseAsMap.put("successMessage", successMessage);

      responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap,HttpStatus.OK);
        
     } else {

                responseAsMap.put("availableEvents", Collections.emptyMap());
                responseAsMap.put("successMessage", "No events available for the specified attendee");

                responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);
            }

        } catch (DataAccessException e) {
            String error = "Error when trying to display your event list and the most likely cause" +
                    e.getMostSpecificCause();
            responseAsMap.put("Error", error);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }
//    @PostMapping("/Attendee/{tutorialId}/tags")
//    public ResponseEntity<Tag> addTag(@PathVariable(value = "tutorialId") Long tutorialId, @RequestBody Tag tagRequest) {
//      Tag tag = tutorialRepository.findById(tutorialId).map(tutorial -> {
//        long tagId = tagRequest.getId();
       
//        // tag is existed
//        if (tagId != 0L) {
//          Tag _tag = tagRepository.findById(tagId)
//              .orElseThrow(() -> new ResourceNotFoundException("Not found Tag with id = " + tagId));
//          tutorial.addTag(_tag);
//          tutorialRepository.save(tutorial);
//          return _tag;
//        }
       
//        // add and create new Tag
//        tutorial.addTag(tagRequest);
//        return tagRepository.save(tagRequest);
//      }).orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with id = " + tutorialId));
 
//      return new ResponseEntity<>(tag, HttpStatus.CREATED);
//    }



   return responseEntity;
   
        
 }

    // 2.2. Attendee registers in an event
    @PostMapping("events/{id}/attendee")
    public ResponseEntity<Map<String, Object>> addAttendeeToEvent(@PathVariable(value = "id") Integer idEvent,
            @RequestBody Attendee attendeeRequest) {

        Map<String, Object> responseAsMap = new HashMap<>();

        try {
            Optional<Event> optionalEvent = eventsService.findById(idEvent);

            if (optionalEvent.isPresent()) {
                Event event = optionalEvent.get();
                int attendeeId = attendeeRequest.getGlobalId();

                if (attendeeId != 0) {
                    Attendee attendee = attendeesService.findByGlobalId(attendeeId);

                    if (attendee != null && eventsService.availableEvents(event)) {
                        event.addAttendees(attendee);
                        eventsService.eventSaved(event);

                        EventDTO eventDTO = new EventDTO();
                        eventDTO.setTitle(event.getTitle());
                        eventDTO.setDescription(event.getDescription());
                        eventDTO.setStartDate(event.getStartDate());
                        eventDTO.setEndDate(event.getEndDate());
                        eventDTO.setStartTime(event.getStartTime());
                        eventDTO.setEndTime(event.getEndTime());
                        eventDTO.setMode(event.getMode());
                        eventDTO.setPlace(event.getPlace());

                        AttendeeDTO attendeeDto = new AttendeeDTO();
                        attendeeDto.setId(attendee.getId());
                        attendeeDto.setName(attendee.getName());
                        attendeeDto.setSurname(attendee.getSurname());
                        attendeeDto.setGlobalId(attendee.getGlobalId());
                        attendeeDto.setMail(attendee.getMail());

                        String successMessage = "The attendee has been successfully added to the event";

                        responseAsMap.put("EventResponse", eventDTO);
                        responseAsMap.put("AttendeeResponse", attendeeDto);
                        responseAsMap.put("successMessage", successMessage);

                        return new ResponseEntity<>(responseAsMap, HttpStatus.ACCEPTED);
                    } else {
                        responseAsMap.put("Message", "Invalid Attendee ID or maximum capacity reached");
                        return new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    responseAsMap.put("Message", "Invalid Attendee ID");
                    return new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);
                }
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
