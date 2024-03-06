package com.example.dao;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entities.Event;

@Repository
public interface EventsDao extends JpaRepository<Event, Integer>{

    // Custom method
    List<Event> findByTitleContaining(String title);

    // Custom query for a custom method
    @Query("SELECT e FROM Event e JOIN e.attendees a WHERE a.globalId = :idGlobal")
    List<Event> findEventsByAttendeeGlobalId( int idGlobal);

}
