package com.example.server.telegram;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.example.server.services.AIAgent;

import jakarta.annotation.PostConstruct;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final Pattern HTML_BR_TAG = Pattern.compile("(?i)<br\\s*/?>");

    @SuppressWarnings("deprecation")
    public TelegramBot() {

    }

    // Force a clean, consistent Telegram-friendly output.
    private static final String RESPONSE_STYLE_GUIDE = """
            You are a assistant running inside a Telegram bot.
            Output MUST be Telegram HTML (not Markdown).
            Be clear and compact. No emojis. No small talk. No "Let me know..." lines.

            If the user asks to list users, respond exactly like:

            <b>Users</b>
                1) <b>[username]</b> — [email]
               <i>Accounts</i>:
                    - [accountName]: [description]

            If there are no users:
            <b>Users</b>
            (none)

            Also i want you to aswer the user based on context of the documents that you have access to,
            so if the user ask about something that is related to the documents you have to answer based on the content of the documents and not based on your knowledge,
            and also answer based on the context of the images that the user send to you
            and based on the tools that you have access to,
            and if you don't have any information about the users question in the documents or the images or the tools you have to say that you don't have enough information to answer the question and you can ask the user to provide more information or more specific question
            {CONTEXT}

            """;

    @Autowired
    private AIAgent aiAgent;

    @Value("${telegram.chatbot.username}")
    private String botUsername;

    @Value("${telegram.chatbot.api-key}")
    private String botToken;

    @Value("${telegram.file-path}")
    private String telegramFilesPath;

    @Autowired
    private VectorStore vectorStore;

    @PostConstruct
    public void registerTelegramBot() {
        if (vectorStore == null) {
            throw new IllegalStateException(
                    "VectorStore bean was not injected. Ensure Spring AI VectorStore is configured and TelegramBot is managed by Spring.");
        }
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update telegramRequest) {
        if (!telegramRequest.hasMessage())
            return;

        String messageText = telegramRequest.getMessage().getText();
        // getting the image from telegram && the caption of the image :
        List<PhotoSize> imagesSentByTele = telegramRequest.getMessage().getPhoto();
        String caption = null;
        List<Media> imageForAgent = new ArrayList<>();
        if (imagesSentByTele != null) {
            caption = telegramRequest.getMessage().getCaption();
            caption = caption == null ? "Could you describe this picture" : caption;
            for (PhotoSize ps : imagesSentByTele) {
                String fileId = ps.getFileId();
                GetFile getFile = new GetFile(fileId);
                try {
                    String filePath = execute(getFile).getFilePath();
                    String textUrl = telegramFilesPath + getBotToken() + "/" + filePath;
                    URL url = new URI(textUrl).toURL();
                    imageForAgent.add(
                            Media.builder()
                                    .id(fileId)
                                    .mimeType(MimeTypeUtils.IMAGE_PNG)
                                    .data(new UrlResource(url))
                                    .build());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }

        // typing asign :
        long chatId = telegramRequest.getMessage().getChatId();
        SendChatAction chatAction = new SendChatAction();
        chatAction.setChatId(chatId);
        chatAction.setAction(ActionType.TYPING);
        try {
            execute(chatAction);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        // building the prompt :
        String query = messageText != null ? messageText : caption;

        // getting the document List
        List<Document> documents = vectorStore.similaritySearch(query);

        String contextText = documents == null ? ""
                : documents.stream()
                        .map(Document::getText)
                        .filter(text -> text != null && !text.isBlank())
                        .collect(Collectors.joining("\n\n---\n\n"));

        Message systemMessage = new SystemPromptTemplate(RESPONSE_STYLE_GUIDE)
                .createMessage(Map.of("CONTEXT", contextText));

        UserMessage userMessage = UserMessage.builder()
                .text(query)
                .media(imageForAgent)
                .build();

        Prompt prompt = new Prompt(List.of(
                systemMessage,
                userMessage));

        String agentResponse = aiAgent.chat(prompt);
        if (agentResponse == null || agentResponse.isEmpty())
            agentResponse = "<b>Error</b>\nSorry, I couldn't generate a response. Please try again.";

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
    private void sendMessageToTelegram(long chatId, String text) {
        String sanitizedText = sanitizeTelegramHtml(text);
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), sanitizedText);
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.disableWebPagePreview();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private static String sanitizeTelegramHtml(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        // Telegram HTML parse mode does not support <br> / <br/>; use newlines instead.
        return HTML_BR_TAG.matcher(html).replaceAll("\n");
    }
}
