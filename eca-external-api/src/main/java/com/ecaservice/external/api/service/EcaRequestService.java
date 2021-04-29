package com.ecaservice.external.api.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapper;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Creates and save request entity.
     *
     * @param evaluationRequestDto - evaluation request dto
     * @return eca request entity
     */
    public EcaRequestEntity createAndSaveRequestEntity(EvaluationRequestDto evaluationRequestDto) {
        var ecaRequestEntity = ecaRequestMapper.map(evaluationRequestDto);
        ecaRequestEntity.setCorrelationId(UUID.randomUUID().toString());
        ecaRequestEntity.setRequestStage(RequestStageType.NOT_SEND);
        ecaRequestEntity.setCreationDate(LocalDateTime.now());
        return evaluationRequestRepository.save(ecaRequestEntity);
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
}
