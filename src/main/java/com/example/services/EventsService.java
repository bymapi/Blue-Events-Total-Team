package com.example.services;

import java.util.List;
import java.util.Optional;

import com.example.entities.Attendee;
import com.example.entities.Event;

public interface EventsService {

    public List<Event> findAllEvents();
    public List<Event> findEventsByTitleContaining(String title);
    public Event eventSaved(Event event);

   
    public List<Event> findEventsByAttendeeGlobalId(int idGlobal);

    public Optional findById(int id);

    public boolean availableEvents (Event event);

    

 

    
    

}
