package com.ecaservice.external.api.test.model;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.ResponseDto;
import lombok.Data;

/**
 * Test data model.
 *
 * @author Roman Batygin
 */
@Data
public class TestDataModel {

    /**
     * Test type
     */
    private TestType testType;

    /**
     * Train data path on file system
     */
    private String trainDataPath;

    /**
     * Request model
     */
    private EvaluationRequestDto request;

    /**
     * Expected response model
     */
    private ResponseDto<EvaluationResponseDto> expectedResponse;
}
