package com.example.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Attendee;
import com.example.entities.Event;
import com.example.entities.EventDTO;
import com.example.entities.EventDTOAdmin;

import com.example.exception.ResourceNotFoundException;
import com.example.helpers.FileDownload;
import com.example.helpers.FileUpLoad;
import com.example.model.FileUploadResponse;
import com.example.services.AttendeesService;
import com.example.services.EventsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventsController {

    private final EventsService eventsService;
    private final FileUpLoad fileUpLoad;
    private final FileDownload fileDownload;
  
    

    // Método enabler para comprobar que devuelve todos los eventos existentes:

    // @GetMapping("/events")
    // public ResponseEntity<List<Event>> findAll(@RequestParam(required = false) String title) {

    //     List<Event> events = new ArrayList<>();

    //     if (title == null) {

    //         eventsService.findAllEvents().forEach(events::add);

    //     } else
    //         eventsService.findEventsByTitleContaining(title).forEach(events::add);

    //     if (events.isEmpty()) {

    //         return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    //     }

    //     return new ResponseEntity<>(events, HttpStatus.OK);
    // }


    //  1-4 Retrieve all events :
@GetMapping("/events")

public ResponseEntity<Map<String,Object>> findAll(@RequestParam(required = false) String title){

    Map<String, Object> responseAsMap = new HashMap<>();
    ResponseEntity<Map<String, Object>> responseEntity = null;

   try {
     
     List<Event> allEvents = eventsService.findAllEvents();
     List<EventDTOAdmin> listEventDto = new ArrayList<>();
    
      
        for (var event :allEvents){
            if (event.getStartDate().isAfter(LocalDate.now()) ||
            (event.getStartDate().isEqual(LocalDate.now()) && event.getStartTime().isBefore(LocalTime.now()))){
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
          
                responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap,HttpStatus.OK);

            }
            
            if (!allEvents.isEmpty()) {
 
                String successMessage = "The list of available events has been successfully created";
                responseAsMap.put("availableEvents",  listEventDto);
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
    responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap,HttpStatus.INTERNAL_SERVER_ERROR);
   } 
    

   return responseEntity;
        
 }

    //------------------------------------------------

    // US 1.2. Create a new internal event
    // it does also validate if it has been created properly

    @PostMapping(consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<Map<String,Object>> createEvent(@Valid @RequestPart(name = "event", required = true) Event event,     
    BindingResult validationResults,
                @RequestPart(name = "file", required = false) MultipartFile file) {

        Map<String,Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String,Object>> responseEntity = null;

        if (file != null) {

            try {
                String fileName = file.getOriginalFilename();
                String fileCode = fileUpLoad.saveFile(fileName, file);
                event.setImagen(fileCode + "-" + fileName) ;

                 FileUploadResponse fileUploadResponse = FileUploadResponse
                       .builder()
                       .fileName(fileCode + "-" + fileName)
                       .downloadURI("/event/downloadFile/" 
                                 + fileCode + "-" + fileName)
                       .size(file.getSize())
                       .build();
            
            responseAsMap.put("info de la imagen: ", fileUploadResponse);           

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // First, we check if the event itself has errors

        if (validationResults.hasErrors()) {

            List<String> errors = new ArrayList<>();
            List<ObjectError> objectErrors = validationResults.getAllErrors();

            objectErrors.forEach(objectError -> errors.add(objectError.getDefaultMessage()));
            
            responseAsMap.put("errors",errors);
            responseAsMap.put("malformed event", event);

            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }

        // As long as the validation goes fine, we can proceed to create our super event
        
        try {

            Event eventCreated = eventsService.eventSaved(event);
            String successMessage = "The event was succesfully created";
            responseAsMap.put("Success Message", successMessage);
            responseAsMap.put("Created event", eventCreated);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.CREATED);
            
        } catch (DataAccessException e) {
            String error = "Something went wrong while creating the event and the most specific cause is: "
            + e.getMostSpecificCause();

            responseAsMap.put("error", error);
            responseAsMap.put("event that was intended to be created", event);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        // Enjoy !
        return responseEntity;
    }

    
      
}


