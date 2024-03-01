package com.example.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Event;
import com.example.services.EventsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PutController {

        private final EventsService eventsService;

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
            Event eventActualizado = eventsService.eventSaved(event);
            String successMessage = "Event was succesfully updated";
            responseAsMap.put("Success Message", successMessage);
            responseAsMap.put("Updated event", eventActualizado);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
        } catch (DataAccessException e) {
            String error = "Error while updating the event and the most specific cause is: "
                    + e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseAsMap.put("Event intended to update", event);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
 
        return responseEntity;
 
    }

}
