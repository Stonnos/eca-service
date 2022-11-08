package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.SimpleEvaluationResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implements handler to compare validation error result.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationResponseComparisonHandler
        extends ApiResponseComparisonTaskHandler<SimpleEvaluationResponseDto> {

    /**
     * Constructor with parameters.
     *
     * @param autoTestRepository - auto test repository bean
     * @param objectMapper       - object mapper bean
     */
    public EvaluationResponseComparisonHandler(AutoTestRepository autoTestRepository,
                                               ObjectMapper objectMapper) {
        super(TaskType.COMPARE_EVALUATION_RESPONSE_RESULT, objectMapper, autoTestRepository);
    }
}
