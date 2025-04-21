package uk.edoatley.openai.rest;

import org.springframework.web.bind.annotation.RestController;
import uk.edoatley.shared.service.AiChatService;
import uk.edoatley.shared.rest.AbstractChatController;

@RestController
public class OpenAIChatController extends AbstractChatController {
    
    public OpenAIChatController(AiChatService chatService) {
        super(chatService);
    }
}