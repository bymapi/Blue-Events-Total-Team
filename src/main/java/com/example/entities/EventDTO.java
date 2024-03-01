package com.example.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor



public class EventDTO {

   

    private String title;

    
    private String description;

    
    
    private LocalDate startDate;

  
    private LocalDate endDate;

   
    private LocalTime startTime;

   
    private LocalTime endTime;

    
    private Mode mode;

    
    private String place;

    private EventStatus eventStatus;;



}
