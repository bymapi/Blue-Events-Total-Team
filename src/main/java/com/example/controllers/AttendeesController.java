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

    // 1.1-create the attendee's profile(persistir)
    
    @PostMapping("/attendee")
    @Transactional
        public ResponseEntity<Map<String,Object>> saveAttendees(@Valid @RequestBody Attendee attendee,
                                    BindingResult validationResults){

            Map<String, Object> responseAsMap = new HashMap<>();
            ResponseEntity<Map<String, Object>> responseEntity = null;

            if (validationResults.hasErrors()) {

                List<String> errores = new ArrayList<>();

                List<ObjectError> objectErrors = validationResults.getAllErrors(); 
                objectErrors.forEach(objectError -> 
                    errores.add(objectError.getDefaultMessage()));

                responseAsMap.put("errores", errores); 
                responseAsMap.put("attendee", attendee);

                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

                return responseEntity;
            }

            try {

                Attendee attendeeSaved = attendeesService.save(attendee);
                String successMessage = "The attendee has persisted well";
                responseAsMap.put("successMessage", successMessage);
                responseAsMap.put("attendee Saved", attendeeSaved);
                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.CREATED);
                
            } catch (DataAccessException e) {
                String error = "Error when trying to save the event attendee and the most likely cause " 
                                + e.getMostSpecificCause();

                responseAsMap.put("error", error);
                responseAsMap.put("Attendee", attendee);

                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return responseEntity;                             
                                    
 }


        //1-1 Administrator can modify an attendee profile using Global ID.
        @PutMapping("/attendee/{globalId}")
        public ResponseEntity<Map<String, Object>> updateAttendee(
                @Valid @RequestBody AttendeeDTO updatedAttendee,
                BindingResult validationResults,
                @PathVariable(name = "globalId", required = true) Integer idGlobal) {
        
            Map<String, Object> responseAsMap = new HashMap<>();
            ResponseEntity<Map<String, Object>> responseEntity;
        
            if (validationResults.hasErrors()) {
                List<String> errores = new ArrayList<>();
                validationResults.getAllErrors().forEach(objectError ->
                        errores.add(objectError.getDefaultMessage()));
        
                responseAsMap.put("errores", errores);
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
//1-1 Administrator can delete an attendee profile using Global ID.

    @DeleteMapping("/attendee/{globalId}")
    public ResponseEntity<Map<String, Object>> deleteAttendeeByIdGlobal(@PathVariable(name = "globalId",
    required = true) Integer idGlobal){

      Map<String,Object> responseAsMap = new HashMap<>();
      ResponseEntity<Map<String,Object>> responseEntity = null;

      try {
        attendeesService.deleteAttendeeByIdGlobal(idGlobal);
        String successMessage = "attendee with id: " +idGlobal +", is removed";
        responseAsMap.put("successMessage", successMessage);
        responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.OK);

      } catch (DataAccessException e) {
        String error = "Error when trying to delete the attendee and the most likely cause" +
        e.getMostSpecificCause();
        responseAsMap.put("error", error);
        responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap,HttpStatus.INTERNAL_SERVER_ERROR);
      }



      return responseEntity;
    }


    // No se pide hay que borrarlo luego
    @GetMapping("/attendees")

    public ResponseEntity<List<Attendee>> findAllStudents(){

    List<Attendee> attendees = attendeesService.findAllAttendees();
    return new ResponseEntity<>(attendees,HttpStatus.OK);
}

//  2-3Retrieve all events of a attendee:
@GetMapping("/attendee/{globalId}/events")

public ResponseEntity<Map<String,Object>> getAllEventsByAttendeeglobalId(@PathVariable(name = "globalId",
                                                   required = true) Integer idGlobal){

    Map<String, Object> responseAsMap = new HashMap<>();
    ResponseEntity<Map<String, Object>> responseEntity = null;

   try {

     List<Event> allAttendeeEvents = eventsService.findEventsByAttendeeGlobalId(idGlobal);
     List<EventDTO> availableEvents = new ArrayList<>();

     for (var attendeeEvent :allAttendeeEvents){
        if(eventsService.availableEvents(attendeeEvent)){
                EventDTO eventDTO = new EventDTO();
                eventDTO.setTitle(attendeeEvent.getTitle());
                eventDTO.setDescription(attendeeEvent.getDescription());
                eventDTO.setStartDate(attendeeEvent.getStartDate());
                eventDTO.setEndDate(attendeeEvent.getEndDate());
                eventDTO.setStartTime(attendeeEvent.getStartTime());
                eventDTO.setEndTime(attendeeEvent.getEndTime());
                eventDTO.setMode(attendeeEvent.getMode());
                eventDTO.setPlace(attendeeEvent.getPlace());

               availableEvents.add(eventDTO);

            }

     }
     if (!availableEvents.isEmpty()) {

        String successMessage = "The list of available events has been successfully created";
        responseAsMap.put("availableEvents", availableEvents);
        responseAsMap.put("successMessage", successMessage);
        return new ResponseEntity<>(responseAsMap, HttpStatus.OK);
        
     } else {

        responseAsMap.put("availableEvents", Collections.emptyMap());
        responseAsMap.put("successMessage", "No events available for the specified attendee");

        responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);
     }
    
  
   } catch (DataAccessException e) {
    String error = "Error when trying to display your event list and the most likely cause" +
    e.getMostSpecificCause();
    responseAsMap.put("Error", error);
    responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap,HttpStatus.INTERNAL_SERVER_ERROR);
   } 
    

   return responseEntity;
        
 }


// 2.2-añadir un attendee a un Evento
 @PostMapping("events/{id}/attendee")
public ResponseEntity<Map<String, Object>> addAttendee(@PathVariable(value = "id") Integer idEvent,
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
                    //attendeeDto.setId(attendee.getId());
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

// 1-5  Consult the list of attendees.
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
 






                                  









    







}
