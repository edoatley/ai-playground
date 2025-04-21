package uk.edoatley.shared.service.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import uk.edoatley.shared.service.AiChatService;

/**
 * Default implementation of the {@link AiChatService} interface.
 * This class provides a default way to interact with a chat client to send and receive messages.
 */
@Service
public class DefaultAiChatService implements AiChatService {
    
    private final ChatClient chatClient;

    public DefaultAiChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    /**
     * Sends a message to the chat client and returns the response.
     *
     * @param message The message to send.
     * @return The response from the chat client.
     */
    @Override
    public String chat(String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}