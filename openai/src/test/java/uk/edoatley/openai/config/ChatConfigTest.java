package uk.edoatley.openai.config;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import uk.edoatley.shared.service.AiChatService;
import uk.edoatley.shared.service.SummarizationService;

import static org.assertj.core.api.Assertions.assertThat;

class ChatConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(ChatConfig.class);

    @Test
    void shouldCreateBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ChatClient.class);
            assertThat(context).hasSingleBean(AiChatService.class);
            assertThat(context).hasSingleBean(SummarizationService.class);
        });
    }
}
