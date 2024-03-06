package com.example.entities;
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
public class AttendeeDTO {

    private String name;

    private String surname;

    private int globalId;

    private String mail;

    private Profile profile;

}
