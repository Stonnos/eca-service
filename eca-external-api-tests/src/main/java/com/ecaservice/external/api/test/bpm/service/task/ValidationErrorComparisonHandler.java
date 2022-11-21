package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implements handler to compare validation error result.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ValidationErrorComparisonHandler extends ApiResponseComparisonTaskHandler<List<ValidationErrorDto>> {

    /**
     * Constructor with parameters.
     *
     * @param objectMapper       - object mapper bean
     * @param autoTestRepository - auto test repository bean
     */
    public ValidationErrorComparisonHandler(ObjectMapper objectMapper,
                                            AutoTestRepository autoTestRepository) {
        super(TaskType.COMPARE_VALIDATION_ERROR_RESULT, objectMapper, autoTestRepository);
    }
}
