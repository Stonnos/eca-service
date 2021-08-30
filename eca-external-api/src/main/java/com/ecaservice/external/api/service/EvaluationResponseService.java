package com.ecaservice.external.api.service;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.mapping.EvaluationStatusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Class for response building.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationResponseService {

    private static final String MODEL_DOWNLOAD_URL_FORMAT = "%s/download-model/%s";

    private final ExternalApiConfig externalApiConfig;
    private final EvaluationStatusMapper evaluationStatusMapper;
    private final EcaRequestService ecaRequestService;

    /**
     * Processes evaluation response.
     *
     * @param correlationId - request correlation id
     * @return evaluation response dto
     */
    public EvaluationResponseDto processResponse(String correlationId) {
        log.debug("Starting to process evaluation response [{}]", correlationId);
        var ecaRequestEntity = ecaRequestService.getByCorrelationId(correlationId);
        var evaluationResponseDtoBuilder = EvaluationResponseDto.builder()
                .requestId(ecaRequestEntity.getCorrelationId());
        var evaluationStatus = evaluationStatusMapper.map(ecaRequestEntity.getRequestStage());
        evaluationResponseDtoBuilder.evaluationStatus(evaluationStatus);
        if (EvaluationStatus.FINISHED.equals(evaluationStatus)) {
            evaluationResponseDtoBuilder.numTestInstances(ecaRequestEntity.getNumTestInstances())
                    .numCorrect(ecaRequestEntity.getNumCorrect())
                    .numIncorrect(ecaRequestEntity.getNumIncorrect())
                    .pctCorrect(ecaRequestEntity.getPctCorrect())
                    .pctIncorrect(ecaRequestEntity.getPctIncorrect())
                    .meanAbsoluteError(ecaRequestEntity.getMeanAbsoluteError())
                    .modelUrl(String.format(MODEL_DOWNLOAD_URL_FORMAT, externalApiConfig.getDownloadBaseUrl(),
                            ecaRequestEntity.getCorrelationId()));
        }
        log.debug("Evaluation [{}] response has been built", correlationId);
        return evaluationResponseDtoBuilder.build();
    }
}
