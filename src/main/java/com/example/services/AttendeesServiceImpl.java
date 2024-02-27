package com.example.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dao.AttendeesDao;
import com.example.entities.Attendee;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class AttendeesServiceImpl implements AttendeesService {

    private final AttendeesDao attendeesDao;

    @Override
    public Attendee save(Attendee attendee) {
        
        return attendeesDao.save(attendee);
    }

    @Override
    public Attendee updateAttendeeByGlobalId(int globalId) {

        return attendeesDao.save(attendeesDao.findByGlobalId(globalId));

    }


    @Override
    public List<Attendee> findAllAttendees() {
        return attendeesDao.findAll();
    }


    @Override
    public void delete(Attendee attendee) {
        
        attendeesDao.delete(attendee);
    }


    @Override
    public void deleteAttendeeByIdGlobal(int globalId) {

       attendeesDao.delete(attendeesDao.findByGlobalId(globalId));

    }

    @Override
    public boolean existsByGlobalId(int globalId) {

        return attendeesDao.existsByGlobalId(globalId);

        

    }

   



}
