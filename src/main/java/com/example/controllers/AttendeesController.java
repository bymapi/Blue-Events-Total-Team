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
import com.example.entities.Event;
import com.example.exception.ResourceNotFoundException;
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

    // create the attendee's profile(persistir)
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


        //Administrator can modify an attendee profile using Global ID.

        @PutMapping("/attendee/{globalId}")
        public ResponseEntity<Map<String,Object>> updateAttendee(@Valid @RequestBody Attendee attendee,
                                    BindingResult validationResults,
                                    @PathVariable(name = "globalId",required = true) Integer idGlobal){

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
                
                //Attendee attendeeUpdated = attendeesService.updateAttendeeByGlobalId(idGlobal);
                attendee.setGlobalId(idGlobal);
                String successMessage = "The attendee has been well modified";
                responseAsMap.put("successMessage", successMessage);
                responseAsMap.put("attendee modified", attendee);
                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.CREATED);
                
            } catch (DataAccessException e) {
                String error = "Error when modifying the attendee's data and the most probable cause" 
                                + e.getMostSpecificCause();

                responseAsMap.put("error", error);
                responseAsMap.put("Attendee", attendee);

                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return responseEntity;                             
                                    
        }


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

     if (!allAttendeeEvents.isEmpty()) {

        Map<LocalDate, List<Event>> eventsSortedByDate = allAttendeeEvents.stream()
        .filter(event -> event.getStartDate().isBefore(LocalDate.now()) ||
        (event.getStartDate().isEqual(LocalDate.now()) && event.getStartTime().isBefore(LocalTime.now())))
        .collect(Collectors.groupingBy(Event::getStartDate));
    
        String successMessage = "the list of available events well created";

      responseAsMap.put("available Events", eventsSortedByDate);
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
    responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap,HttpStatus.INTERNAL_SERVER_ERROR);
   } 
    

   return responseEntity;
        
 }

 // 2.2

 /* @PostMapping("events/{id}/attendee")
 public ResponseEntity<Map<String,Object>>  addAttendee(@PathVariable(value = "id") Integer idEvent, 
                                    @RequestBody Attendee attendeeRequest) {

Map<String, Object> responseAsMap = new HashMap<>();
ResponseEntity<Map<String, Object>> responseEntity = null;

  eventsService.findById(idEvent).map(e -> {

    int attendeeId = attendeeRequest.getGlobalId();

   Optional<Event> opcionalEvent= eventsService.findById(idEvent);
   var event = opcionalEvent.get();

    // Si el idglobal existe
    if (attendeeId != 0){

        Attendee attendee1 = attendeesService.findByGlobalId(attendeeId);
        if (eventsService.availableEvents(event)) {
            event.addAttendees(attendee1);
            eventsService.eventSaved(event);

            String successMessage = "The attendee has been successfully added to the event";

            responseAsMap.put("Attendee", attendee1);
            responseAsMap.put("successMessage", successMessage);
      
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap,HttpStatus.ACCEPTED);
            }
            else{

                responseAsMap.put("Message", "Maximum capacity allowed");
        
                responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.NOT_ACCEPTABLE);

            }    
        }
    })
       



return ResponseEntity;
 

 } */

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

                    String successMessage = "The attendee has been successfully added to the event";

                    responseAsMap.put("Attendee", attendee);
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
