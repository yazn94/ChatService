package com.chat.chatservice.database;

import com.chat.chatservice.model.ContactDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Configuration
public class DAO {
    private final JdbcTemplate jdbcTemplate;
    private final String DOCTOR_AUTH = "doctor_auth";
    private final String PARENT_AUTH = "parent_auth";
    private final String CHILD_AUTH = "child_auth";
    private final String CHILD_PRO = "child_profile";
    private final String PARENT_PRO = "parent_profile";
    private final String DOCTOR_PRO = "doctor_profile";

    @Autowired
    public DAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ArrayList<ContactDTO> getParentContacts(String parentEmail) {
        String query = "SELECT doctorEmail FROM " + CHILD_PRO + " WHERE parentEmail = ?";
        try {
            ArrayList<String> doctorsEmails = (ArrayList<String>) jdbcTemplate.queryForList(query, new Object[]{parentEmail}, String.class);
            ArrayList<ContactDTO> doctors = new ArrayList<>();

            for (String doctorEmail : doctorsEmails) {
                ContactDTO dto = new ContactDTO();
                dto.setEmail(doctorEmail);
                query = "SELECT username FROM " + DOCTOR_PRO + " WHERE email = ?";
                dto.setUsername(jdbcTemplate.queryForObject(query, new Object[]{doctorEmail}, String.class));
                doctors.add(dto);
            }
            return doctors;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public ArrayList<ContactDTO> getDoctorContacts(String doctorEmail) {
        String query = "SELECT parentEmail FROM " + CHILD_PRO + " WHERE doctorEmail = ?";
        try {
            ArrayList<String> parentsEmails = (ArrayList<String>) jdbcTemplate.queryForList(query, new Object[]{doctorEmail}, String.class);
            ArrayList<ContactDTO> parents = new ArrayList<>();

            for (String parentEmail : parentsEmails) {
                ContactDTO dto = new ContactDTO();
                dto.setEmail(parentEmail);
                query = "SELECT username FROM " + PARENT_PRO + " WHERE email = ?";
                dto.setUsername(jdbcTemplate.queryForObject(query, new Object[]{parentEmail}, String.class));
                parents.add(dto);
            }
            return parents;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
