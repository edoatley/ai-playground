package uk.edoatley.shared.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.edoatley.shared.model.ChatResponse;
import uk.edoatley.shared.service.AiChatService;

@RestController
@RequestMapping("/api/chat")
public class AbstractChatController {

    private final AiChatService chatService;

    public AbstractChatController(AiChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody String message) {
        return new ChatResponse(this.chatService.chat(message));
    }
}