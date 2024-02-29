package com.example.services;

import java.util.List;
import java.util.Optional;

import com.example.entities.Event;

public interface EventsService {

    public List<Event> findAllEvents();
    public List<Event> findEventsByTitleContaining(String title);
    Event findById(int id);
    public Event eventSaved(Event event);
    public void deleteEvent(Event event);
    public List<Event> findEventsByAttendeeGlobalId(int idGlobal);
    public boolean availableEvents (Event event);

 

    
    

}
