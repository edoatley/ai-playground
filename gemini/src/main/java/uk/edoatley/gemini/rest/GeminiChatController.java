package uk.edoatley.gemini.rest;

import org.springframework.web.bind.annotation.RestController;
import uk.edoatley.shared.rest.AbstractChatController;
import uk.edoatley.shared.service.AiChatService;

@RestController
public class GeminiChatController extends AbstractChatController {
    
    public GeminiChatController(AiChatService chatService) {
        super(chatService);
    }
}
