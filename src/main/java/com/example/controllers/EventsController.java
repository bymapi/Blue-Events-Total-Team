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
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

import com.example.entities.Event;
import com.example.entities.EventDTOAdmin;
import com.example.helpers.FileDownload;
import com.example.helpers.FileUpLoad;
import com.example.model.FileUploadResponse;
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

    /*
     * US 1.2. Create a new internal event.
     * As Administrator I want to create new internal events.
     */

    @PostMapping( value = "/event" , consumes = "multipart/form-data")
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
            
            responseAsMap.put("Information sur l'image : ", fileUploadResponse);           

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // First, we check if the event itself has errors

        if (validationResults.hasErrors()) {

            List<String> errors = new ArrayList<>();
            List<ObjectError> objectErrors = validationResults.getAllErrors();

            objectErrors.forEach(objectError -> errors.add(objectError.getDefaultMessage()));
            
            responseAsMap.put("erreurs",errors);
            responseAsMap.put("Événement malformé", event);

            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }

        // As long as the validation goes fine, we can proceed to create our super event
        
        try {

            Event eventCreated = eventsService.eventSaved(event);
            String successMessage = "L'événement a été créé avec succès";
            responseAsMap.put("Success Message", successMessage);
            responseAsMap.put("Created event", eventCreated);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.CREATED);
            
        } catch (DataAccessException e) {
            String error = "Quelque chose s'est mal passé lors de la création de l'événement et la cause la plus spécifique est : "
            + e.getMostSpecificCause();

            responseAsMap.put("erreur", error);
            responseAsMap.put("Événement qui était censé être créé", event);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        
        return responseEntity;
    }

   
    /*
     * US 1.3. Modify an event.
     * As Administrator I want to modify or delete an event.
     */
    // 1.3.a) Administrator can modify events by its id
    @PutMapping("/événement/{id}")
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

            responseAsMap.put("erreurs", errors);
            responseAsMap.put("Événement malformé", event);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }

        try {
            event.setId(idEvent);
            var originalTarget = event.getTarget();
            Event eventUpdated = eventsService.eventSaved(event);
            if (!eventUpdated.getTarget().equals(originalTarget)) {

                String error = "ERROR";

                responseAsMap.put("erreur", error);

                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

                return responseEntity;

            } else {

                Event eventUpdated2 = eventsService.eventSaved(eventUpdated);

                String successMessage = "L'événement a été mis à jour avec succès.";
                responseAsMap.put("Success Message", successMessage);
                responseAsMap.put("Événement mis à jour.", eventUpdated);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
            }

        } catch (DataAccessException e) {
            String error = "Erreur lors de la mise à jour de l'événement et la cause la plus précise est : "
                    + e.getMostSpecificCause();
            responseAsMap.put("erreur", error);
            responseAsMap.put("Événement destiné à être mis à jour.", event);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    // 1.3.b) Administrator can delete events by its id
    @DeleteMapping("/événement/{id}")
    public ResponseEntity<Map<String, Object>> deleteEventById(
            @PathVariable(name = "idGlobal", required = true) Integer eventId) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {
            eventsService.deleteEventById(eventId);
            String successMessage = "événement avec id: " + eventId + ", a été supprimé";
            responseAsMap.put("successMessage", successMessage);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);

        } catch (DataAccessException e) {
            String error = "Erreur lors de la tentative de suppression de l'événement et la cause la plus probable " +
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
     @GetMapping("/événements/disponibles")
    public ResponseEntity<Map<String, Object>> findAllAvailableEvents(@RequestParam(required = false) String title) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {

            List<Event> allEvents = eventsService.findAllEvents();
            List<EventDTOAdmin> listEventDto = new ArrayList<>();

            for (Event event : allEvents) {
                if (event.getStartDate().isAfter(LocalDate.now()) ||
                        (event.getStartDate().isEqual(LocalDate.now())
                                && event.getStartTime().isAfter(LocalTime.now()))) {
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

                }
            }

                if (!allEvents.isEmpty()) {

                    String successMessage = "La liste des événements disponibles a été créée avec succès";
                    responseAsMap.put("availableEvents", listEventDto);
                    responseAsMap.put("successMessage", successMessage);
                    return new ResponseEntity<>(responseAsMap, HttpStatus.OK);

                } else {

                    responseAsMap.put("availableEvents", Collections.emptyMap());
                    responseAsMap.put("successMessage", "Aucun événement disponible ");

                    responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);
                }
            

        } catch (DataAccessException e) {
            String error = "Erreur lors de la tentative d'affichage de votre liste d'événements et la cause la plus probable " +
                    e.getMostSpecificCause();
            responseAsMap.put("Erreur", error);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    } 

     /**
     *  Implementa filedownnload end point API 
     **/    
    @GetMapping("/telechargerFichier/{codeFichier}")
    public ResponseEntity<?> downloadFile(@PathVariable(name = "codeFichier") String fileCode) {

        Resource resource = null;

        try {
            resource = fileDownload.getFileAsResource(fileCode);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("Fichier non trouvé  ", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue )
        .body(resource);

    }  


}
