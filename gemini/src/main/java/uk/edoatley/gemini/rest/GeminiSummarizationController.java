package uk.edoatley.gemini.rest;

import org.springframework.web.bind.annotation.RestController;

import uk.edoatley.shared.rest.AbstractSummarizationController;
import uk.edoatley.shared.service.SummarizationService;

@RestController
public class GeminiSummarizationController extends AbstractSummarizationController {
    
    public GeminiSummarizationController(SummarizationService summarizationService) {
        super(summarizationService);
    }
}
