package com.example.server.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import com.example.server.tools.AITools;
import lombok.Data;
@Service
@Data
public class AIAgent {

    private AITools aiTools;
    private ChatClient chatClient;
    
    public AIAgent(
        ChatClient.Builder builder,
        ChatMemory memory
        , AITools aiTools){
        this.chatClient = builder
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(memory)
                .build()
            )
            .defaultTools(aiTools)
            .build();
        
    }

    public String chat(String message){
        return chatClient
            .prompt()
            .user(message)
            .call().content();
    }
}
