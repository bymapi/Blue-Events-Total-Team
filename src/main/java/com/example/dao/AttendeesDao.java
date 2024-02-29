package com.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entities.Attendee;

@Repository
public interface AttendeesDao extends JpaRepository<Attendee,Integer> {

    Attendee findByGlobalId(int globalId);

    boolean existsByGlobalId(int globalId);



}
