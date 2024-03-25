package com.chat.chatservice.controller;

import com.chat.chatservice.model.UserType;
import com.chat.chatservice.service.UserContactsHelper;
import com.chat.chatservice.util.JwtTokenUtil;
import com.chat.chatservice.util.StringOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class ChatController {
    private final UserContactsHelper userContactsHelper;

    @Autowired
    public ChatController(UserContactsHelper userContactsHelper) {
        this.userContactsHelper = userContactsHelper;
    }

    @GetMapping("/chat/user/contacts/{email}")
    public ResponseEntity<String> getContacts(@RequestHeader("Authorization") String token, @PathVariable String email) {
        token = StringOperations.removeBearerIfExist(token);
        token = StringOperations.removeQuotesIfExist(token);

        if (!JwtTokenUtil.validateToken(token) || !JwtTokenUtil.getEmailFromToken(token).equals(email)) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        if (JwtTokenUtil.getUserTypeFromToken(token) == UserType.DOCTOR) {
            return ResponseEntity.ok().body(userContactsHelper.getDoctorContacts(email));
        } else if (JwtTokenUtil.getUserTypeFromToken(token) == UserType.PARENT) {
            return ResponseEntity.ok().body(userContactsHelper.getParentContacts(email));
        } else {
            return ResponseEntity.badRequest().body("Child users are not allowed to chat");
        }
    }
}
