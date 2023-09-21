package com.ecaservice.external.api.test.model;

import com.ecaservice.external.api.dto.AbstractEvaluationRequestDto;
import com.ecaservice.external.api.test.entity.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
 * Abstract test data model.
 *
 * @param <REQ>  - request generic type
 * @author Roman Batygin
 */
@Data
public abstract class AbstractTestDataModel<REQ extends AbstractEvaluationRequestDto> implements Serializable {

    /**
     * Display name (Test description)
     */
    private String displayName;

    /**
     * Train data path on file system
     */
    private String trainDataPath;

    /**
     * Request model
     */
    private REQ request;

    /**
     * Expected response code
     */
    private ResponseCode expectedResponseCode;
}
