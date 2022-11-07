package com.ecaservice.external.api.test.model;

import com.ecaservice.external.api.dto.AbstractEvaluationRequestDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.dto.SimpleEvaluationResponseDto;
import lombok.Data;

import java.io.Serializable;

/**
 * Abstract test data model.
 *
 * @param <REQ>  - request generic type
 * @param <RESP> - response generic type
 * @author Roman Batygin
 */
@Data
public abstract class AbstractTestDataModel<REQ extends AbstractEvaluationRequestDto, RESP extends SimpleEvaluationResponseDto>
        implements Serializable {

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
    private REQ request;

    /**
     * Expected response model
     */
    private ResponseDto<RESP> expectedResponse;
}
