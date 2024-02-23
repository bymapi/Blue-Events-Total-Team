package com.example.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Event;
import com.example.services.EventsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventsController {

    private final EventsService eventsService;

    @GetMapping("/events")
    public ResponseEntity<List<Event>> findAll(@RequestParam(required = false) String title) {

        List<Event> events = new ArrayList<>();

        if (title == null) {

            eventsService.findAllEvents().forEach(events::add);

        } else
            eventsService.findEventsByTitleContaining(title).forEach(events::add);

        if (events.isEmpty()) {

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    // Create a new internal event
    @PostMapping("/events")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {

        Event eventCreated = eventsService.eventSaved(event);
        
        return new ResponseEntity<>(eventCreated, HttpStatus.CREATED);
    }

}
