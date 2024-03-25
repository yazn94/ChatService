package com.chat.chatservice;

import com.chat.chatservice.model.UserType;
import com.chat.chatservice.util.JwtTokenUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatServiceApplication {

    public static void main(String[] args) {
        System.out.println(
                JwtTokenUtil.generateToken("ahmadwesamalia@gmail.com", "ahmad", UserType.DOCTOR));

        SpringApplication.run(ChatServiceApplication.class, args);
    }

}
