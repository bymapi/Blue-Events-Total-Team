package com.example.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dao.EventsDao;
import com.example.entities.Event;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService{

    private final EventsDao eventsDao;

    @Override
    public Event eventSaved(Event event) {
        
        return eventsDao.save(event);
    }

    @Override
    public List<Event> findAllEvents() {
        
        return eventsDao.findAll();
    }



}
