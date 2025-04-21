package uk.edoatley.shared.rest;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import uk.edoatley.shared.model.SummaryResponse;
import uk.edoatley.shared.service.SummarizationService;

import java.io.IOException;

@RequestMapping("/api/summarize")
public abstract class AbstractSummarizationController {
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String PDF_MIME_TYPE = "application/pdf";
    private static final String TEXT_MIME_TYPE = "text/plain";
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    protected final SummarizationService summarizationService;

    protected AbstractSummarizationController(SummarizationService summarizationService) {
        this.summarizationService = summarizationService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SummaryResponse summarize(MultipartFile file) throws IOException {
        validateFile(file);
        String content = extractContentFromFile(file);
        String summary = summarizationService.summarize(content);
        return new SummaryResponse(summary);
    }

    protected void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
        }
        String contentType = file.getContentType();
        if (!PDF_MIME_TYPE.equals(contentType)) {
            throw new IllegalArgumentException("Only PDF files are supported");
        }
    }

    protected String extractContentFromFile(MultipartFile file) throws IOException {
        if (PDF_MIME_TYPE.equals(file.getContentType())) {
            return extractTextFromPdf(file);
        }
        return new String(file.getBytes());
    }

    protected String extractTextFromPdf(MultipartFile file) throws IOException {
        byte[] pdfBytes = file.getBytes();
        try (PDDocument pdfDocument = Loader.loadPDF(pdfBytes)) {
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