package uk.edoatley.openai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OpenAIApplication {

    public static void main(String[] args) {
        System.out.println("OPENAI_API_KEY: " + System.getenv("OPENAI_API_KEY"));
        SpringApplication.run(OpenAIApplication.class, args);
    }

}