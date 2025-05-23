package com.example.pricecomparator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pricecomparator.controller.CompareController;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CompareController.class)
public class CompareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // JAVA <=> JSON
    @Autowired
    private ObjectMapper objectMapper;


}
