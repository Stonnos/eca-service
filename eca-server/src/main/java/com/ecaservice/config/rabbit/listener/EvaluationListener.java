package com.ecaservice.config.rabbit.listener;

import com.ecaservice.config.rabbit.Queues;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.service.evaluation.EvaluationRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Rabbit MQ listener for evaluation request messages.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationListener {

    private final RabbitTemplate rabbitTemplate;
    private final EvaluationRequestService evaluationRequestService;

    @Inject
    public EvaluationListener(RabbitTemplate rabbitTemplate,
                              EvaluationRequestService evaluationRequestService) {
        this.rabbitTemplate = rabbitTemplate;
        this.evaluationRequestService = evaluationRequestService;
    }

    /**
     * Handles evaluation request message.
     *
     * @param evaluationRequest - evaluation request
     */
    @RabbitListener(queues = Queues.EVALUATION_REQUEST_QUEUE)
    public void handleMessage(EvaluationRequest evaluationRequest) {
        log.info("Received request for classifier [{}] evaluation with data [{}]",
                evaluationRequest.getClassifier().getClass().getSimpleName(),
                evaluationRequest.getData().relationName());
        EvaluationResponse evaluationResponse = evaluationRequestService.processRequest(evaluationRequest);
        log.info("Evaluation response [{}] with status [{}] has been built.", evaluationResponse.getRequestId(),
                evaluationResponse.getStatus());
        rabbitTemplate.convertAndSend(Queues.EVALUATION_RESULTS_QUEUE, evaluationResponse);
    }
}