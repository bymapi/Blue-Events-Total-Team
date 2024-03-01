package com.example.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.entities.Attendee;

@Repository
public interface AttendeesDao extends JpaRepository<Attendee,Integer> {

    Attendee findByGlobalId(int globalId);

    boolean existsByGlobalId(int globalId);

    @Query("SELECT a FROM Attendee a JOIN a.events e WHERE e.id = :eventId")
    List<Attendee> findByEventsId(int eventId);



}
