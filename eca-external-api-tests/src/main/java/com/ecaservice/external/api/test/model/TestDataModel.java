package com.ecaservice.external.api.test.model;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResultsResponseDto;
import com.ecaservice.external.api.dto.ResponseDto;
import lombok.Data;

import java.io.Serializable;

/**
 * Test data model.
 *
 * @author Roman Batygin
 */
@Data
public class TestDataModel implements Serializable {

    /**
     * Display name (Test description)
     */
    private String displayName;

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
    private ResponseDto<EvaluationResultsResponseDto> expectedResponse;
}
