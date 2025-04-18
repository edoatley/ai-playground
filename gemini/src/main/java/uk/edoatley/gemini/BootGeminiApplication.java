package uk.edoatley.gemini;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BootGeminiApplication {

	public static void main(String[] args) {
		System.out.println("GOOGLE_APPLICATION_CREDENTIALS: " + System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
		SpringApplication.run(BootGeminiApplication.class, args);
	}

}
