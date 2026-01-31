package com.example.server.services;

import java.util.Arrays;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import lombok.Data;
@Service
@Data
public class AIAgent {

    private ChatClient chatClient;
    
    public AIAgent(
        ChatClient.Builder builder,
        ChatMemory memory,
        ToolCallbackProvider tools
    ){
        Arrays.stream(tools.getToolCallbacks()).forEach(tool->{
            System.out.println("-------------------------------------------------------");
            System.out.println(tool.getToolDefinition());

        });
        this.chatClient = builder
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(memory)
                .build()
            ).defaultToolCallbacks(tools)
            .build();
        
    }

    public String chat(String message){
        return chatClient
            .prompt()
            .user(message)
            .call().content();
    }
}
