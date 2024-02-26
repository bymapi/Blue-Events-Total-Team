package com.example.security.dtos;

import java.util.List;

import com.example.entities.Attendee;
import com.example.entities.Event;
import com.example.security.entities.OurUser;
import com.example.security.entities.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ReqResp {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String email;
    private Role role;
    private String password;
    private List<Event> events;
    private OurUser ourUser;
}