package com.ecaservice.load.test.mapping;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.load.test.entity.LoadTestEntity;
import org.mapstruct.Mapper;

/**
 * Evaluation request mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface EvaluationRequestMapper {

    /**
     * Maps load test entity to evaluation request.
     *
     * @param loadTestEntity - load test entity
     * @return evaluation request
     */
    EvaluationRequest map(LoadTestEntity loadTestEntity);
}
