package uk.edoatley.openai.rest;

import org.springframework.web.bind.annotation.RestController;
import uk.edoatley.shared.rest.AbstractSummarizationController;
import uk.edoatley.shared.service.SummarizationService;

@RestController
public class OpenAISummarizationController extends AbstractSummarizationController {
    
    public OpenAISummarizationController(SummarizationService summarizationService) {
        super(summarizationService);
    }
}
