package com.example.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.dao.EventsDao;
import com.example.entities.Event;
import com.example.entities.EventStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventsServiceImpl implements EventsService {

    private final EventsDao eventsDao;

    @Override
    public Event eventSaved(Event event) {

        return eventsDao.save(event);
    }

    @Override
    public List<Event> findAllEvents() {

        return eventsDao.findAll();
    }

    @Override
    public List<Event> findEventsByTitleContaining(String title) {

        return eventsDao.findByTitleContaining(title);
    }

    @Override
    public List<Event> findEventsByAttendeeGlobalId(int idGlobal) {
        return eventsDao.findEventsByAttendeeGlobalId(idGlobal);
    }

    @Override
    public Optional<Event> findById(int id) {
        return eventsDao.findById(id);
    }

    @Override
    public boolean availableEvents(Event event) {

        if ((event.getAttendees().size() < event.getMaximumNumberOfAttendees()
                && event.getEventStatus() == EventStatus.ACTIVÉ) &&
                (event.getStartDate().isAfter(LocalDate.now()) ||
                        (event.getStartDate().isEqual(LocalDate.now()) &&
                                event.getStartTime().isAfter(LocalTime.now())))) {
            return true;

        } else
            return false;

    }

    @Override
    public void deleteEventById(int eventId) {

       eventsDao.deleteById(eventId);

    }

    @Override
    public boolean existsById(int eventId) {

        return eventsDao.existsById(eventId);

    }
}
