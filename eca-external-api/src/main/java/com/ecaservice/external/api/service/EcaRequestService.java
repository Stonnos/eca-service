package com.ecaservice.external.api.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Eca request service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EcaRequestService {

    private final FileDataService fileDataService;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Creates and save evaluation request entity.
     *
     * @return eca request entity
     */
    public EcaRequestEntity createAndSaveEvaluationRequestEntity() {
        String correlationId = UUID.randomUUID().toString();
        log.info("Starting to save evaluation request for correlation id [{}]", correlationId);
        var ecaRequestEntity = new EvaluationRequestEntity();
        ecaRequestEntity.setCorrelationId(correlationId);
        ecaRequestEntity.setRequestStage(RequestStageType.READY);
        ecaRequestEntity.setCreationDate(LocalDateTime.now());
        evaluationRequestRepository.save(ecaRequestEntity);
        log.info("Evaluation request has been saved for correlation id [{}]", correlationId);
        return ecaRequestEntity;
    }

    /**
     * Gets evaluation request by correlation id.
     *
     * @param correlationId - correlation id
     * @return evaluation request entity
     */
    public EvaluationRequestEntity getByCorrelationId(String correlationId) {
        return evaluationRequestRepository.findByCorrelationId(correlationId)
                .orElseThrow(() -> new EntityNotFoundException(EcaRequestEntity.class, correlationId));
    }

    /**
     * Deletes classifier model file.
     *
     * @param evaluationRequestEntity - evaluation request entity
     */
    @Transactional
    public void deleteClassifierModel(EvaluationRequestEntity evaluationRequestEntity) {
        log.info("Starting to delete evaluation request [{}] classifier model file",
                evaluationRequestEntity.getCorrelationId());
        String classifierAbsolutePath = evaluationRequestEntity.getClassifierAbsolutePath();
        evaluationRequestEntity.setClassifierAbsolutePath(null);
        evaluationRequestEntity.setDeletedDate(LocalDateTime.now());
        evaluationRequestRepository.save(evaluationRequestEntity);
        fileDataService.delete(classifierAbsolutePath);
        log.info("Evaluation request [{}] classifier model file has been deleted",
                evaluationRequestEntity.getCorrelationId());
    }
}
