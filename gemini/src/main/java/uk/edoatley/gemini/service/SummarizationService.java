package uk.edoatley.gemini.service;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class SummarizationService {

    private static final Prompt SUMMARIZE_PROMPT = new Prompt("Summarize the following content in a concise manner, highlighting the key points and main ideas.");
    private final ChatClient chatClient;

    public SummarizationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Summarizes the given content by sending it to the ChatClient.
     *
     * @param content The content to summarize.
     * @return The summary of the content.
     */
    public String summarize(String content) {
        // Example of sending content to the ChatClient and receiving a summary
        return this.chatClient.prompt(SUMMARIZE_PROMPT)
                .user(content)
                .call()
                .content();
    }
}
