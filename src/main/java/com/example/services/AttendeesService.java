package com.example.services;

import java.util.List;

import com.example.entities.Attendee;

public interface AttendeesService {

    public Attendee save(Attendee attendee);

    public Attendee updateAttendeeByGlobalId(int globalId);

    public List<Attendee> findAllAttendees();

    public void delete(Attendee attendee);

    public void deleteAttendeeByIdGlobal(int globalId);

    public boolean existsByGlobalId(int globalId);

    Attendee findByGlobalId(int globalId);

    

    

}
