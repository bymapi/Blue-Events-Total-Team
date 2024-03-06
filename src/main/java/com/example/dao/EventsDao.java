package com.example.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.entities.Attendee;
import com.example.entities.Event;

@Repository
public interface EventsDao extends JpaRepository<Event, Integer>{

    List<Event> findByTitleContaining(String title);

    

     @Query("SELECT e FROM Event e JOIN e.attendees a WHERE a.globalId = :idGlobal")
     List<Event> findEventsByAttendeeGlobalId( int idGlobal);


     //List<Attendee> findByEventsId(int idEvent);

     
    @Query("SELECT a FROM Attendee a JOIN a.events e WHERE e.id = :eventId")
    List<Attendee> findByEventsId(int eventId);

    
     

  

     

    

}
