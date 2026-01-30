package com.example.server.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@RestController
@RequestMapping("/api/v2")
@Data
public class ChatController {
    public ChatClient chatClient;
    
    // chatClient injection, for communicate with LLM.
    public ChatController(ChatClient.Builder builder){
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public String chat(
            @RequestParam(name = "message") String message
        ){
        return chatClient
            .prompt()
            .user(message)
            .call().content();
    }

}
