package com.ecaservice.external.api.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.ExperimentRequestDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapper;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
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

    private final EcaRequestMapper ecaRequestMapper;
    private final ObjectStorageService objectStorageService;
    private final EcaRequestRepository ecaRequestRepository;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Creates and save evaluation request entity.
     *
     * @param evaluationRequestDto - evaluation request dto
     * @return eca request entity
     */
    public EcaRequestEntity createAndSaveEvaluationRequestEntity(EvaluationRequestDto evaluationRequestDto) {
        String correlationId = UUID.randomUUID().toString();
        log.info("Starting to save evaluation request for correlation id [{}]", correlationId);
        var ecaRequestEntity = ecaRequestMapper.map(evaluationRequestDto);
        initializeRequest(ecaRequestEntity, correlationId);
        ecaRequestRepository.save(ecaRequestEntity);
        log.info("Evaluation request has been saved for correlation id [{}]", correlationId);
        return ecaRequestEntity;
    }

    /**
     * Creates and save evaluation request entity with use optimal classifier options flag.
     *
     * @return eca request entity
     */
    public EcaRequestEntity createAndSaveEvaluationOptimizerRequestEntity() {
        String correlationId = UUID.randomUUID().toString();
        log.info("Starting to save evaluation request for correlation id [{}]", correlationId);
        var ecaRequestEntity = new EvaluationRequestEntity();
        ecaRequestEntity.setUseOptimalClassifierOptions(true);
        initializeRequest(ecaRequestEntity, correlationId);
        ecaRequestRepository.save(ecaRequestEntity);
        log.info("Evaluation request has been saved for correlation id [{}]", correlationId);
        return ecaRequestEntity;
    }

    /**
     * Creates and save evaluation request entity.
     *
     * @param experimentRequestDto - evaluation request dto
     * @return eca request entity
     */
    public EcaRequestEntity createAndSaveExperimentRequestEntity(ExperimentRequestDto experimentRequestDto) {
        String correlationId = UUID.randomUUID().toString();
        log.info("Starting to save experiment [{}] request for correlation id [{}]",
                experimentRequestDto.getExperimentType(), correlationId);
        var ecaRequestEntity = ecaRequestMapper.map(experimentRequestDto);
        initializeRequest(ecaRequestEntity, correlationId);
        ecaRequestRepository.save(ecaRequestEntity);
        log.info("Experiment [{}] request has been saved for correlation id [{}]",
                experimentRequestDto.getExperimentType(),correlationId);
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
        String classifierPath = evaluationRequestEntity.getClassifierPath();
        evaluationRequestEntity.setClassifierPath(null);
        evaluationRequestEntity.setDeletedDate(LocalDateTime.now());
        evaluationRequestRepository.save(evaluationRequestEntity);
        objectStorageService.removeObject(classifierPath);
        log.info("Evaluation request [{}] classifier model file has been deleted",
                evaluationRequestEntity.getCorrelationId());
    }

    private void initializeRequest(EcaRequestEntity ecaRequestEntity, String correlationId) {
        ecaRequestEntity.setCorrelationId(correlationId);
        ecaRequestEntity.setRequestStage(RequestStageType.READY);
        ecaRequestEntity.setCreationDate(LocalDateTime.now());
    }
}
