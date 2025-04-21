package uk.edoatley.gemini.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.edoatley.shared.service.AiChatService;
import uk.edoatley.shared.service.SummarizationService;
import uk.edoatley.shared.service.impl.DefaultAiChatService;
import uk.edoatley.shared.service.impl.DefaultSummarizationService;

@Configuration
public class ChatConfig {
    
    @Bean
    ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }

    @Bean
    AiChatService chatService(ChatClient chatClient) {
        return new DefaultAiChatService(chatClient);
    }
    
    @Bean
    SummarizationService summarizationService(ChatClient chatClient) {
        return new DefaultSummarizationService(chatClient);
    }
}
