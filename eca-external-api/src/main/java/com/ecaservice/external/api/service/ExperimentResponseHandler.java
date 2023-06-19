package com.ecaservice.external.api.service;

import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.external.api.entity.ExperimentRequestEntity;
import com.ecaservice.external.api.mapping.EcaRequestMapper;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Experiment response handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentResponseHandler extends AbstractEcaResponseHandler<ExperimentRequestEntity, ExperimentResponse> {

    /**
     * Constructor with parameters.
     *
     * @param ecaRequestRepository - eca request repository
     * @param requestStageHandler  - request stage handler
     * @param ecaRequestMapper     - eca request mapper
     */
    public ExperimentResponseHandler(EcaRequestRepository ecaRequestRepository,
                                     RequestStageHandler requestStageHandler,
                                     EcaRequestMapper ecaRequestMapper) {
        super(ecaRequestRepository, requestStageHandler, ecaRequestMapper);
    }

    @Override
    protected void internalHandleSuccessResponse(ExperimentRequestEntity requestEntity,
                                                 ExperimentResponse experimentResponse) {
        log.info("Starting to process success experiment response [{}]", requestEntity.getCorrelationId());
        Assert.notNull(experimentResponse.getDownloadUrl(),
                String.format("Expected not null experiment download url for correlation id [%s]",
                        requestEntity.getCorrelationId()));
        requestEntity.setExperimentDownloadUrl(experimentResponse.getDownloadUrl());
        log.info("Success experiment response [{}] has been processed", requestEntity.getCorrelationId());
    }
}
