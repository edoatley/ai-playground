package uk.edoatley.gemini.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import uk.edoatley.gemini.rest.model.SummaryResponse;
import uk.edoatley.gemini.service.SummarizationService;

import java.io.IOException;

@RestController
@RequestMapping("/api/summarize")
public class SummarizationController {

    private final SummarizationService summarizationService;

    public SummarizationController(SummarizationService summarizationService) {
        this.summarizationService = summarizationService;
    }

    /**
     * This endpoint allows a user to upload a file (text or PDF) and get a summary of its content.
     * The file is sent as a multipart/form/data request, and the response will contain the summary.
     *
     * @param file The uploaded file (text or PDF).
     * @return A summary of the file content.
     * @throws IOException If an error occurs while reading the file.
     */
    @PostMapping
    public SummaryResponse summarize(MultipartFile file) throws IOException {
        // Extract content from the uploaded file
        String content = extractContentFromFile(file);

        // Call the summarization service
        String summary = summarizationService.summarize(content);

        // Return the summary in the response
        return new SummaryResponse(summary);
    }

    private String extractContentFromFile(MultipartFile file) throws IOException {
        // Check the file type and extract content accordingly
        if (file.getOriginalFilename().endsWith(".pdf")) {
            // Use a PDF library like Apache PDFBox to extract text from PDF
            return extractTextFromPdf(file);
        } else {
            // Assume it's a plain text file
            return new String(file.getBytes());
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        // Example using Apache PDFBox
        try (var pdfDocument = org.apache.pdfbox.pdmodel.PDDocument.load(file.getInputStream())) {
            return new org.apache.pdfbox.text.PDFTextStripper().getText(pdfDocument);
        }
    }
}
