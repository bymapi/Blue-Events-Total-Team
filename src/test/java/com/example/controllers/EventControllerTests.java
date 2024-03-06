package com.example.controllers;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc

@AutoConfigureTestDatabase(replace = Replace.NONE)

public class EventControllerTests {

    





}
