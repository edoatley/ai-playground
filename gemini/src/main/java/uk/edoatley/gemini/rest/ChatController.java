package uk.edoatley.gemini.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.edoatley.gemini.rest.model.ChatResponse;
import uk.edoatley.gemini.service.SimpleChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final SimpleChatService simpleChatService;

    public ChatController(SimpleChatService simpleChatService) {
        this.simpleChatService = simpleChatService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody String message) {
        return new ChatResponse(this.simpleChatService.chat(message));
    }
}
