package com.example.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entities.Event;
import com.example.entities.Target;

@Repository
public interface EventsDao extends JpaRepository<Event, Integer> {

    List<Event> findByTitleContaining(String title);

    @Query("SELECT e FROM Event e JOIN e.attendees a WHERE a.globalId = :idGlobal")
    List<Event> findEventsByAttendeeGlobalId(int idGlobal);

}
