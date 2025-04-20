package uk.edoatley.gemini.rest;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.MediaType;
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
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String PDF_MIME_TYPE = "application/pdf";
    private static final String TEXT_MIME_TYPE = "text/plain";

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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SummaryResponse summarize(MultipartFile file) throws IOException {
        validateFile(file);
        String content = extractContentFromFile(file);
        String summary = summarizationService.summarize(content);
        return new SummaryResponse(summary);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
        }
        String contentType = file.getContentType();
        if (!PDF_MIME_TYPE.equals(contentType) && !TEXT_MIME_TYPE.equals(contentType)) {
            throw new IllegalArgumentException("Only PDF and text files are supported");
        }
    }

    private String extractContentFromFile(MultipartFile file) throws IOException {
        if (PDF_MIME_TYPE.equals(file.getContentType())) {
            return extractTextFromPdf(file);
        }
        return new String(file.getBytes());
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument pdfDocument = PDDocument.load(file.getInputStream())) {
            if (pdfDocument.isEncrypted()) {
                throw new IllegalArgumentException("Encrypted PDFs are not supported");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(pdfDocument);
        } catch (IOException e) {
            throw new IOException("Failed to process PDF file: " + e.getMessage(), e);
        }
    }
}
