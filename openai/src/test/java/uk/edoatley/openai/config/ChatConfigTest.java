package uk.edoatley.openai.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.edoatley.shared.service.AiChatService;
import uk.edoatley.shared.service.SummarizationService;

import static org.assertj.core.api.Assertions.assertThat;

class ChatConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class, ChatConfig.class);

    @Test
    void shouldCreateBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ChatClient.class);
            assertThat(context).hasSingleBean(AiChatService.class);
            assertThat(context).hasSingleBean(SummarizationService.class);
        });
    }

    @Configuration
    static class TestConfig {
        @Bean
        ChatClient.Builder chatClientBuilder() {
            ChatClient.Builder mockBuilder = Mockito.mock(ChatClient.Builder.class);
            ChatClient mockClient = Mockito.mock(ChatClient.class);
            Mockito.when(mockBuilder.build()).thenReturn(mockClient);
            return mockBuilder;
        }
    }
}
