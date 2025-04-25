# Spring AI and MCP

A Spring Boot 3 project using Spring AI and MCP to demonstrate its value. The goal is to create a simple application where an AI model can use a predefined "tool" (a Java function) to get information it doesn't inherently possess, fitting within a two-week timeframe.

## Project Idea: "Smart Task Assistant"

This assistant will take natural language requests and use an external (mocked or simple) tool to fetch details about specific tasks or projects. This demonstrates MCP by allowing the AI to query a "system" (our Spring Boot app) for specific, structured data.

## Core Value Demonstration of MCP:

1.     Structured Interaction: The AI won't just guess; it will specifically request to use a defined function (getTaskDetails) when it recognizes the user wants information about a task.
2. Accessing External/Dynamic Data: The AI itself doesn't store the task details. It relies on the application's function (our tool) to provide this information, which could theoretically come from a database, an external API, etc.
3. Constrained Capabilities: We explicitly define what tools the AI can use, preventing it from hallucinating functions or trying to access unauthorized resources.

## Project Scope (Feasible in 2 Weeks):

1. Basic Spring Boot 3 Setup: Standard project structure.
2. Spring AI Integration: Configure connection to an AI provider (e.g., OpenAI, Ollama - Ollama is great for local testing without API keys).
3. MCP Function Definition: Create a Java Bean that acts as a "Task Service" with a method like getTaskDetails(String taskId).
4. Chat Endpoint: A simple REST controller to receive user prompts.
5. Service Layer: Orchestrates the interaction with ChatClient, enabling the MCP function.
6. Mock Data: The getTaskDetails function will return predefined mock data for simplicity. (Connecting to a real DB/API could be a stretch goal).
7. Basic Testing: Simple tests to ensure the endpoint works and the function is called when appropriate.

## Steps & Implementation Details:

1. Project Setup (Using Spring Initializr):

-    Go to start.spring.io.
-    Project: Maven
-    Language: Java
-    Spring Boot: 3.2.x or later
-    Group: com.example
-    Artifact: mcp-task-assistant
-    Packaging: Jar
-    Java: 17 or later
-    Dependencies:
-        `Spring Web`
-        `Spring AI OpenAI Starter` (or `Spring AI Ollama Starter`, `Spring AI Azure OpenAI Starter`, etc.)
-        `Lombok` (Optional, for boilerplate code reduction)

2. Configure AI Provider (application.properties or application.yml):

For OpenAI:
    
```properties
spring.ai.openai.api-key=YOUR_OPENAI_API_KEY
spring.ai.openai.chat.options.model=gpt-4o # Or gpt-3.5-turbo, etc.
```

For Ollama (Assuming Ollama server is running locally):

```properties
    spring.ai.ollama.base-url=http://localhost:11434
    spring.ai.ollama.chat.options.model=llama3 # Or mistral, etc. (Ensure the model supports function calling)
```
*Note: Make sure the Ollama model you choose supports function calling. Llama3, newer Mistral models, etc., generally do.*

3. Define the MCP Function (Tool):

Create a configuration class to define your function as a Spring Bean.

```java
package com.example.mcptaskassistant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.Map;
import java.util.function.Function;

@Configuration
public class TaskTools {

    // Mock data source
    private static final Map<String, Map<String, Object>> taskDatabase = Map.of(
            "TASK-001", Map.of("id", "TASK-001", "summary", "Setup project repository", "status", "Completed", "assignee", "Alice"),
            "TASK-002", Map.of("id", "TASK-002", "summary", "Implement authentication", "status", "In Progress", "assignee", "Bob"),
            "PROJ-A", Map.of("id", "PROJ-A", "summary", "Develop MCP demo application", "status", "Active", "lead", "Charlie")
    );

    @Bean
    @Description("Get details for a specific task or project ID") // Essential for the AI
    public Function<GetTaskDetailsRequest, GetTaskDetailsResponse> getTaskDetails() {
        return request -> {
            System.out.println("MCP Function 'getTaskDetails' called with ID: " + request.taskId); // Log invocation
            Map<String, Object> details = taskDatabase.getOrDefault(request.taskId(), Map.of("error", "Task or Project not found"));
            return new GetTaskDetailsResponse(details);
        };
    }

    // Define Request and Response Objects for type safety and clarity
    @Description("Request object for the getTaskDetails function")
    public record GetTaskDetailsRequest(
            @Description("The unique ID of the task or project (e.g., TASK-001, PROJ-A)") String taskId
    ) {}

    public record GetTaskDetailsResponse(Map<String, Object> details) {}
}
```

Notes:

-    @Description on the bean method is crucial. It tells the AI what the function does.
-    @Description on the record parameters tells the AI what arguments are expected.
-    The function takes a request object (GetTaskDetailsRequest) and returns a response object (GetTaskDetailsResponse). This structured approach is recommended.
-    We use simple mock data here.

4. Create the Service Layer:

This service will use the ChatClient and enable the function calling.

```java
package com.example.mcptaskassistant.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class AssistantService {

    private final ChatClient chatClient;

    // Inject the ChatClient builder and customize it for function calling
    public AssistantService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                // .defaultFunctions("getTaskDetails") // Register the function globally if desired
                .build();
    }

    public String getAssistance(String userPrompt) {
        // System prompt to guide the AI (optional but helpful)
        String systemPrompt = """
                You are a helpful Task Assistant.
                If the user asks for details about a specific task or project,
                use the 'getTaskDetails' function available to you.
                Provide the information clearly based on the function's result.
                If the task ID is not found, inform the user.
                For general conversation, respond normally.
                """;

        ChatResponse response = chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                // Crucially, enable the function for this specific call:
                .functions("getTaskDetails") // Specify the bean name(s) of the function(s) to make available
                .call()
                .chatResponse();

        // Extract the response content
        // Note: Spring AI handles the function call execution automatically when configured like this.
        // The response object will contain the *final* AI message after it potentially used the function.
        return response.getResult().getOutput().getContent();
    }
}
```

Notes:

-    We inject ChatClient.Builder to configure the client.
-    functions("getTaskDetails") in the .prompt() chain explicitly tells the ChatClient that the getTaskDetails bean (which we defined earlier) is available for this specific -request. Spring AI uses the bean name here.
-    Spring AI handles the multi-turn conversation with the LLM (User Prompt -> AI thinks Function Call -> App Executes Function -> Function Result -> AI generates final response) behind the scenes.

5. Create the REST Controller:

```java
package com.example.mcptaskassistant.controller;

import com.example.mcptaskassistant.service.AssistantService;
import lombok.RequiredArgsConstructor; // Or use constructor injection
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assistant")
@RequiredArgsConstructor // Lombok annotation for constructor injection
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/chat")
    public ChatResponsePayload chat(@RequestBody ChatRequestPayload request) {
        String response = assistantService.getAssistance(request.prompt());
        return new ChatResponsePayload(response);
    }

    // Simple request/response DTOs
    public record ChatRequestPayload(String prompt) {}
    public record ChatResponsePayload(String response) {}
}
```

6. Run and Test:

- Start the Spring Boot application.

-   If using Ollama, ensure the Ollama server is running (ollama serve) and the model you configured is pulled (ollama pull llama3).

-   Send POST requests to http://localhost:8080/api/v1/assistant/chat (or your configured port) with a JSON body like:

- Request needing the function:

```JSON
{
  "prompt": "Can you tell me the status of TASK-002?"
}
```

*Expected Behavior: The application logs should show "MCP Function 'getTaskDetails' called with ID: TASK-002". The response should be something like: "The task TASK-002 (Implement authentication) is currently In Progress and assigned to Bob."*

- Request for a non-existent task:

```JSON
{
  "prompt": "What's happening with TASK-999?"
}
```

*Expected Behavior: Logs show the function call. Response indicates not found: "I couldn't find any details for task or project ID TASK-999."*

- General chat request:

```JSON
{
    "prompt": "Hello, how are you?"
}
```

*Expected Behavior: No function call logged. A standard conversational response from the AI.*

## Feasibility within 2 Weeks:

- Week 1: Setup project, configure Spring AI and provider, implement the basic controller/service structure, define the MCP function bean with mock data, basic manual testing via HTTP client (Postman, curl).
- Week 2: Refine prompts, add logging, implement basic unit/integration tests (e.g., testing the controller endpoint and mocking the service), potentially explore error handling (what if the function fails?), documentation/cleanup.

This scope is well within a two-week timeframe, focusing purely on demonstrating the core MCP concept with Spring AI. Expanding it to use a real database or external API would add complexity but could be a next step.