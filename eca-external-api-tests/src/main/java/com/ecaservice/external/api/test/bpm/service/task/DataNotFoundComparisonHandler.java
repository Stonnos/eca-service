package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.SimpleEvaluationResponseDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implements handler to compare data not found result.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class DataNotFoundComparisonHandler extends ApiResponseComparisonTaskHandler<SimpleEvaluationResponseDto> {

    /**
     * Constructor with parameters.
     *
     * @param objectMapper       - object mapper bean
     * @param autoTestRepository - auto test repository bean
     */
    public DataNotFoundComparisonHandler(ObjectMapper objectMapper,
                                         AutoTestRepository autoTestRepository) {
        super(TaskType.COMPARE_DATA_NOT_FOUND_RESULT, objectMapper, autoTestRepository);
    }
}
