package com.example.server.telegram;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
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

    @Value("${telegram.file-path}")
    private String telegramFilesPath;


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
        // getting the image from telegram && the caption of the image :
        List<PhotoSize> imagesSentByTele =  telegramRequest.getMessage().getPhoto();
        String capiton = telegramRequest.getMessage().getCaption();

        List<Media> imageForAgent = new ArrayList<>();
        for(PhotoSize ps : imagesSentByTele){
           String fileId = ps.getFileId();
           GetFile getFile = new GetFile(fileId);
           try {
                String filePath = execute(getFile).getFilePath();
                String textUrl = telegramFilesPath + getBotToken()+"/"+ filePath;
                URL url = new URI(textUrl).toURL();
                imageForAgent.add(
                    Media
                        .builder()
                        .id(fileId)
                        .mimeType(MimeTypeUtils.ALL)
                        .data(new UrlResource(url))
                        .build()
                    );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // building the prompt : 
            
        String query = messageText != null ? messageText : capiton;
        UserMessage userMessage = UserMessage
            .builder()
            .text(query)
            .media(imageForAgent)
            .build()
        ;

        String agentResponse =  aiAgent.chat(new Prompt(userMessage));
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
