package com.ecaservice.external.api.mq.listener;

import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.external.api.entity.ExperimentRequestEntity;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.repository.ExperimentRequestRepository;
import com.ecaservice.external.api.service.ExperimentResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Experiment response message listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentResponseListener extends AbstractResponseListener<ExperimentRequestEntity, ExperimentResponse> {

    private final ExperimentRequestRepository experimentRequestRepository;
    private final ExperimentResponseHandler experimentResponseHandler;

    /**
     * Constructor with parameters.
     *
     * @param ecaRequestRepository        - eca request repository
     * @param experimentRequestRepository - experiment request repository
     * @param experimentResponseHandler   - experiment response handler
     */
    public ExperimentResponseListener(EcaRequestRepository ecaRequestRepository,
                                      ExperimentRequestRepository experimentRequestRepository,
                                      ExperimentResponseHandler experimentResponseHandler) {
        super(ecaRequestRepository);
        this.experimentRequestRepository = experimentRequestRepository;
        this.experimentResponseHandler = experimentResponseHandler;
    }

    @Override
    @RabbitListener(queues = "${queue.experimentResponseQueue}")
    public void handleResponseMessage(ExperimentResponse experimentResponse, Message message) {
        super.handleResponseMessage(experimentResponse, message);
    }

    @Override
    protected Optional<ExperimentRequestEntity> getRequestEntity(String correlationId) {
        return experimentRequestRepository.findByCorrelationId(correlationId);
    }

    @Override
    protected void handleResponse(ExperimentRequestEntity requestEntity, ExperimentResponse ecaResponse) {
        experimentResponseHandler.handleResponse(requestEntity, ecaResponse);
    }
}
