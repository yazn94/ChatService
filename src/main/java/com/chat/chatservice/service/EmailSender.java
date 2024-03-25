package com.chat.chatservice.service;

import com.chat.chatservice.exceptions.EmailSendingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String toEmail, String subject, String body) throws EmailSendingException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("autismcare24@gmail.com");
            message.setTo(toEmail);
            message.setText(body);
            message.setSubject(subject);
            javaMailSender.send(message);
            System.out.println("Mail Sent...");
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send email to " + toEmail, e);
        }
    }
}