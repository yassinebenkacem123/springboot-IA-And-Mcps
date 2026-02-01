package com.example.server.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.example.server.services.AIAgent;

import jakarta.annotation.PostConstruct;
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


    @PostConstruct
    public void registerTelegramBot(){
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);        
        }
    }
   
    @Override
    public void onUpdateReceived(Update telegramRequest) {
        if(!telegramRequest.hasMessage()) return;

        String messageText = telegramRequest.getMessage().getText();
        String agentResponse =  aiAgent.chat(messageText);
        long chatId = telegramRequest.getMessage().getChatId();
        sendMessageToTelegram(chatId, agentResponse);

    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    // method for sending message to telegram :
    private void sendMessageToTelegram(long chatId, String text){
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new  RuntimeException(e);
        }

    }
}
