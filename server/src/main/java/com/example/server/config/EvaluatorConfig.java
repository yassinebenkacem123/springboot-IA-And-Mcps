package com.example.server.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EvaluatorConfig {
    @Bean
    public RelevancyEvaluator relevancyEvaluator(ChatClient.Builder chatClientBuilder) {
        return RelevancyEvaluator.builder()
        .chatClientBuilder(chatClientBuilder)
        .build();
    }

    @Bean
    public FactCheckingEvaluator factCheckingEvaluator(ChatClient.Builder chatClientBuilder) {
        return FactCheckingEvaluator.builder(chatClientBuilder).build();
    }

}
