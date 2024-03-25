package com.chat.chatservice.service;

import com.chat.chatservice.database.DAO;
import com.chat.chatservice.model.ContactDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserContactsHelper {
    private final DAO dao;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserContactsHelper(DAO dao, ObjectMapper objectMapper) {
        this.dao = dao;
        this.objectMapper = objectMapper;
    }

    public String getParentContacts(String email) {
        List<ContactDTO> contacts = dao.getParentContacts(email);
        Map<String, Object> response = new HashMap<>();

        if (contacts != null && !contacts.isEmpty()) {
            response.put("contacts", contacts);
        } else {
            response.put("contacts", new ArrayList<>());
        }

        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle serialization error
            return "{\"message\": \"Error serializing contacts.\"}";
        }
    }

    public String getDoctorContacts(String email) {
        List<ContactDTO> contacts = dao.getDoctorContacts(email);
        Map<String, Object> response = new HashMap<>();

        if (contacts != null && !contacts.isEmpty()) {
            response.put("contacts", contacts);
        } else {
            response.put("contacts", new ArrayList<>());
        }

        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle serialization error
            return "{\"message\": \"Error serializing contacts.\"}";
        }
    }
}
