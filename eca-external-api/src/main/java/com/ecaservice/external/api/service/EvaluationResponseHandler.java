package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.mapping.EcaRequestMapper;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Evaluation response handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationResponseHandler extends AbstractEcaResponseHandler<EvaluationRequestEntity, EvaluationResponse> {

    private final EcaRequestMapper ecaRequestMapper;

    /**
     * Constructor with parameters.
     *
     * @param ecaRequestRepository - eca request repository
     * @param requestStageHandler  - request stage handler
     * @param ecaRequestMapper     - eca request mapper
     * @param ecaRequestMapper1    - eca request mapper
     */
    public EvaluationResponseHandler(EcaRequestRepository ecaRequestRepository,
                                     RequestStageHandler requestStageHandler,
                                     EcaRequestMapper ecaRequestMapper,
                                     EcaRequestMapper ecaRequestMapper1) {
        super(ecaRequestRepository, requestStageHandler, ecaRequestMapper);
        this.ecaRequestMapper = ecaRequestMapper1;
    }

    @Override
    protected void internalHandleSuccessResponse(EvaluationRequestEntity requestEntity,
                                                 EvaluationResponse evaluationResponse) {
        log.info("Starting to process success evaluation response [{}]", requestEntity.getCorrelationId());
        ecaRequestMapper.update(evaluationResponse, requestEntity);
        log.info("Success evaluation response [{}] has been processed", requestEntity.getCorrelationId());
    }
}
