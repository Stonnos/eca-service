package com.ecaservice.external.api.mq.listener;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.service.EvaluationResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Evaluation response message listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationResponseListener extends AbstractResponseListener<EvaluationRequestEntity, EvaluationResponse> {

    private final EvaluationRequestRepository evaluationRequestRepository;
    private final EvaluationResponseHandler evaluationResponseHandler;

    /**
     * Constructor with parameters,
     *
     * @param evaluationRequestRepository - evaluation request repository
     * @param evaluationResponseHandler   - evaluation response handler
     */
    public EvaluationResponseListener(EvaluationRequestRepository evaluationRequestRepository,
                                      EvaluationResponseHandler evaluationResponseHandler) {
        this.evaluationRequestRepository = evaluationRequestRepository;
        this.evaluationResponseHandler = evaluationResponseHandler;
    }

    @Override
    @RabbitListener(queues = "${queue.evaluationResponseQueue}")
    public void handleResponseMessage(EvaluationResponse evaluationResponse, Message message) {
        super.handleResponseMessage(evaluationResponse, message);
    }

    @Override
    protected Optional<EvaluationRequestEntity> getRequestEntity(String correlationId) {
        return evaluationRequestRepository.findByCorrelationId(correlationId);
    }

    @Override
    protected void handleResponse(EvaluationRequestEntity requestEntity, EvaluationResponse ecaResponse) {
        evaluationResponseHandler.handleResponse(requestEntity, ecaResponse);
    }
}
