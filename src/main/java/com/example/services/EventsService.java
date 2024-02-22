package com.example.services;

import java.util.List;

import com.example.entities.Event;

public interface EventsService {

    public List<Event> findAllEvents();
    public Event eventSaved(Event event);

}
