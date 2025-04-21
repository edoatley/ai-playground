package uk.edoatley.gemini.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import uk.edoatley.shared.service.SummarizationService;
import uk.edoatley.shared.model.SummaryResponse;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummarizationControllerTest {

    @Mock
    private SummarizationService summarizationService;

    @InjectMocks
    private GeminiSummarizationController controller;

    @Test
    void shouldSuccessfullyExtractContentFromPdfFile() throws IOException {
        // Given
        String expectedSummary = "This is the summary";
        when(summarizationService.summarize(anyString())).thenReturn(expectedSummary);

        try (InputStream pdfInputStream = getClass().getResourceAsStream("/FDBP.pdf")) {
            MockMultipartFile file = new MockMultipartFile(
                "file",
                "FDBP.pdf",
                "application/pdf",
                pdfInputStream
            );

            // When
            SummaryResponse response = controller.summarize(file);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.summary()).isEqualTo(expectedSummary);
        }
    }

    @Test
    void shouldRejectNonPdfFile() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );

        // When/Then
        assertThatThrownBy(() -> controller.summarize(file))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Only PDF files are supported");
    }

    @Test
    void shouldHandleEmptyFile() {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "empty.pdf",
            "application/pdf",
            new byte[0]
        );

        // When/Then
        assertThatThrownBy(() -> controller.summarize(file))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Empty file");
    }
}