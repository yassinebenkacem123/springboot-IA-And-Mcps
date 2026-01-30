package com.example.server.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.server.services.AIAgent;

import lombok.Data;

@RestController
@RequestMapping("/api/v2")
@Data
public class ChatController {
    @Autowired
    private AIAgent aiAgent;
    


    @GetMapping("/chat")
    public String chat(
            @RequestParam(name = "message") String message
        ){
        return aiAgent.chat(message);
    }

}
