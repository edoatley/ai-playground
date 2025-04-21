package uk.edoatley.gemini.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.edoatley.shared.model.ChatResponse;
import uk.edoatley.shared.service.AiChatService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeminiChatControllerTest {

    @Mock
    private AiChatService chatService;

    @InjectMocks
    private GeminiChatController controller;

    @Test
    void shouldHandleChatRequest() {
        // Given
        String request = "Hello";
        ChatResponse expectedResponse = new ChatResponse("Hello back!");
        when(chatService.chat(any(String.class))).thenReturn(expectedResponse.message());

        // When
        ChatResponse response = controller.chat(request);

        // Then
        assertThat(response).isEqualTo(expectedResponse);
    }
}
