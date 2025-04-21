package uk.edoatley.gemini.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import uk.edoatley.gemini.rest.model.SummaryResponse;
import uk.edoatley.gemini.service.SummarizationService;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SummarizationControllerTest {

    @Mock
    private SummarizationService summarizationService;

    @InjectMocks
    private SummarizationController controller;

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
}