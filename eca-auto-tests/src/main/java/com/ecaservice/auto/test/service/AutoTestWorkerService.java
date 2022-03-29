package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.service.rabbit.RabbitSender;
import com.ecaservice.base.model.EcaRequest;
import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.base.model.visitor.EcaRequestVisitor;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;

/**
 * Auto test worker service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoTestWorkerService {

    private final RabbitSender rabbitSender;
    private final EvaluationRequestService evaluationRequestService;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;

    /**
     * Sends experiment test request to mq.
     *
     * @param testId     - test id
     * @param ecaRequest - eca request
     */
    public void sendRequest(long testId, EcaRequest ecaRequest) {
        ecaRequest.visit(new EcaRequestVisitor() {
            @Override
            public void visitEvaluationRequest(EvaluationRequest evaluationRequest) {
                internalSendRequest(testId, evaluationRequest, rabbitSender::sendEvaluationRequest);
            }

            @Override
            public void visitExperimentRequest(ExperimentRequest experimentRequest) {
                internalSendRequest(testId, experimentRequest, rabbitSender::sendExperimentRequest);
            }

            @Override
            public void visitInstancesRequest(InstancesRequest instancesRequest) {
                throw new NotImplementedException("Instances request sending not implemented");
            }
        });
    }

    private <R> void internalSendRequest(long testId, R request, BiConsumer<R, String> sender) {
        var baseEvaluationRequestEntity = baseEvaluationRequestRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentRequestEntity.class, testId));
        log.info("Starting to send request with correlation id [{}]", baseEvaluationRequestEntity.getCorrelationId());
        try {
            baseEvaluationRequestEntity.setStarted(LocalDateTime.now());
            sender.accept(request, baseEvaluationRequestEntity.getCorrelationId());
            baseEvaluationRequestEntity.setStageType(RequestStageType.REQUEST_SENT);
            baseEvaluationRequestEntity.setExecutionStatus(ExecutionStatus.IN_PROGRESS);
            baseEvaluationRequestRepository.save(baseEvaluationRequestEntity);
            log.info("Request with correlation id [{}] has been sent", baseEvaluationRequestEntity.getCorrelationId());
        } catch (Exception ex) {
            log.error("Unknown error while sending request with correlation id [{}]: {}",
                    baseEvaluationRequestEntity.getCorrelationId(), ex.getMessage());
            evaluationRequestService.finishWithError(baseEvaluationRequestEntity, ex.getMessage());
        }
    }
}
