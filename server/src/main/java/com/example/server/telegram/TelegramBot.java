package com.example.server.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.example.server.services.AIAgent;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {


    @Autowired
    private AIAgent aiAgent;

    @Value("${telegram.chatbot.username}")
    private String botUsername;

    @Value("${telegram.chatbot.api-key}")
    private  String botToken;

   
    @Override
    public void onUpdateReceived(Update update) {
      
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
