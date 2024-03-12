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
     * US 1.1. Gérer un nouveau profil de participant.
     * En tant qu'administrateur, je souhaite créer un profil de participant à l'événement.
     */

    // 1.1.a) L'administrateur est chargé de gérer les participants - Créer.
    @PostMapping("/participant")
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
            String successMessage = "Le participant a été créé avec succès";
            responseAsMap.put("successMessage", successMessage);
            responseAsMap.put("attendee Saved", attendeeSaved);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.CREATED);

        } catch (DataAccessException e) {
            String error = "Erreur lors de la tentative d'enregistrement du participant à l'événement et la cause la plus probable est"
                    + e.getMostSpecificCause();

            responseAsMap.put("error", error);
            responseAsMap.put("Attendee", attendee);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    // 1.1.b) L'administrateur peut modifier le profil du participant en utilisant l'identifiant global
    @PutMapping("/participant/{idGlobal}")
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
                String errorMessage = "Participant non trouvé avec l'identifiant global" + idGlobal;
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

            String successMessage = "Le participant a été modifié avec succès";
            responseAsMap.put("successMessage", successMessage);
            responseAsMap.put("attendeeModified", attendeeUpdated);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);

        } catch (DataAccessException e) {
            String errorMessage = "Erreur lors de la modification des données du participant. Cause: " + e.getMostSpecificCause();
            responseAsMap.put("error", errorMessage);
            responseAsMap.put("Attendee", updatedAttendee);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    // 1.1.c) L'administrateur peut supprimer le profil du participant en utilisant l'identifiant global
    @DeleteMapping("/participant/{idGlobal}")
    public ResponseEntity<Map<String, Object>> deleteAttendeeByIdGlobal(
            @PathVariable(name = "globalId", required = true) Integer idGlobal) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {
            attendeesService.deleteAttendeeByIdGlobal(idGlobal);
            String successMessage = "participant avec idGlobal: " + idGlobal + ", a été supprimé";
            responseAsMap.put("successMessage", successMessage);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);

        } catch (DataAccessException e) {
            String error = "Erreur lors de la tentative de suppression du participant et la cause la plus probable est" +
                    e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    /*
     * US 1.5. Consulter la liste des participants.
     * En tant qu'administrateur, je souhaite consulter tous les participants à un événement..
     */
    @GetMapping("événement/{id}/participants")
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

                String successMessage = "La liste des participants à l'événement a été créé avec succès";
                responseAsMap.put("availableEventAttendees", attendeesDTOList);
                responseAsMap.put("successMessage", successMessage);
                return new ResponseEntity<>(responseAsMap, HttpStatus.OK);
            } else {
                responseAsMap.put("availableEventAttendees", Collections.emptyList());
                responseAsMap.put("successMessage", "Aucun participant n'est disponible pour l'événement spécifié");
                return new ResponseEntity<>(responseAsMap, HttpStatus.OK);
            }

        } catch (Exception e) {
            responseAsMap.put("erreur", "Une erreur s'est produite lors du traitement de la demande. " + e.getMessage());
            return new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * US 2.1. Le Participant consulte les événements disponibles.
     * En tant que participant, je souhaite consulter toutes les classes disponibles pour les dates futures.
     *  Disponible signifie un statut activé.
     */
    @GetMapping("/événements/disponible/participant/{id}")
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

                    String successMessage = "Il y a des événements disponibles";

                    responseAsMap.put("EventResponse", eventDTO);
                    responseAsMap.put("successMessage", successMessage);

                    return new ResponseEntity<>(responseAsMap, HttpStatus.OK);
                } else {
                    responseAsMap.put("Message", "Il n'y a pas d'événements disponibles");
                    return new ResponseEntity<>(responseAsMap, HttpStatus.NOT_FOUND);
                }

            }

        } catch (Exception e) {
            responseAsMap.put("Message", "Une erreur s'est produite lors du traitement de la demande");
            return new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    /*
     * US 2.2. Attendee registers in an event.
     * As an Attendee I want to register in an event on a specific title and day.
     */
    @PostMapping("événements/{id}/inscription")
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
                        attendeeDto.setName(attendee.getName());
                        attendeeDto.setSurname(attendee.getSurname());
                        attendeeDto.setGlobalId(attendee.getGlobalId());
                        attendeeDto.setMail(attendee.getMail());

                        String successMessage = "Le participant a été ajouté avec succès à l'événement";

                        responseAsMap.put("EventResponse", eventDTO);
                        responseAsMap.put("AttendeeResponse", attendeeDto);
                        responseAsMap.put("successMessage", successMessage);

                        return new ResponseEntity<>(responseAsMap, HttpStatus.ACCEPTED);
                    } else {
                        responseAsMap.put("Message", "Identifiant de participant invalide ou capacité maximale atteinte");
                        return new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);
                    }
                } else {
                    responseAsMap.put("Message", "Identifiant de participant invalide");
                    return new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);
                }
            } else {
                responseAsMap.put("Message", "Événement non trouvé");
                return new ResponseEntity<>(responseAsMap, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            responseAsMap.put("Message", "Une erreur est survenue lors du traitement de la demande");
            return new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * US 2.3. Attendee consults his/her events.
     * As an Attendee I would like to check my events for future dates.
     */
     @GetMapping("/participant/{idGlobal}/événements")
    public ResponseEntity<Map<String, Object>> getAllEventsByAttendeeglobalId(
            @PathVariable(name = "globalId", required = true) Integer idGlobal) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {

            List<Event> allAttendeeEvents = eventsService.findEventsByAttendeeGlobalId(idGlobal);
            List<EventDTO> availableEvents = new ArrayList<>();

            for (var attendeeEvent : allAttendeeEvents) {
                if (eventsService.availableEvents(attendeeEvent)) {
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

                String successMessage = "La liste des événements disponibles a été créée avec succès";
                responseAsMap.put("availableEvents", availableEvents);
                responseAsMap.put("successMessage", successMessage);
                return new ResponseEntity<>(responseAsMap, HttpStatus.OK);

            } else {

                responseAsMap.put("availableEvents", Collections.emptyMap());
                responseAsMap.put("successMessage", "Aucun événement disponible pour le participant spécifié");

                responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);
            }

        } catch (DataAccessException e) {
            String error = "L'erreur lors de l'affichage de votre liste d'événements pourrait être due à un problème de communication avec la base de données ou à des données manquantes dans le système" +
                    e.getMostSpecificCause();
            responseAsMap.put("Error", error);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    } 
    
}
