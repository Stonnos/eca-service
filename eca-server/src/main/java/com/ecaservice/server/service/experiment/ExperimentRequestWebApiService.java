package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.server.dto.CreateExperimentRequestDto;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.config.audit.AuditCodes.CREATE_EXPERIMENT_REQUEST;

/**
 * Experiment request web api service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentRequestWebApiService {

    private final ExperimentProcessManager experimentProcessManager;
    private final ExperimentRepository experimentRepository;

    /**
     * Creates experiment request.
     *
     * @param experimentRequestDto - experiment request dto
     * @return created experiment result
     */
    @Audit(value = CREATE_EXPERIMENT_REQUEST, correlationIdKey = "#result.requestId")
    public CreateExperimentResultDto createExperiment(CreateExperimentRequestDto experimentRequestDto) {
        String requestId = UUID.randomUUID().toString();
        putMdc(TX_ID, requestId);
        putMdc(EV_REQUEST_ID, requestId);
        log.info("Starting to create experiment [{}] request for instances [{}], type [{}], evaluation method [{}]",
                requestId, experimentRequestDto.getInstancesUuid(), experimentRequestDto.getExperimentType(),
                experimentRequestDto.getEvaluationMethod());
        experimentProcessManager.createExperimentWebRequest(requestId, experimentRequestDto);
        var experiment = experimentRepository.findByRequestId(requestId)
                        .orElseThrow(() -> new EntityNotFoundException(Experiment.class, requestId));
        log.info("Experiment request [{}] has been created for instances uuid [{}].", requestId,
                experimentRequestDto.getInstancesUuid());
        return CreateExperimentResultDto.builder()
                .id(experiment.getId())
                .requestId(experiment.getRequestId())
                .build();
    }
}
