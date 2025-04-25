package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.server.dto.CreateExperimentRequestDto;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.UserService;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.config.audit.AuditCodes.CANCEL_EXPERIMENT_REQUEST;
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

    private final UserService userService;
    private final ExperimentProcessManager experimentProcessManager;
    private final ExperimentMapper experimentMapper;
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
        log.info("Starting to create experiment [{}] request for instances [{}], type [{}], evaluation method [{}]",
                requestId, experimentRequestDto.getInstancesUuid(), experimentRequestDto.getExperimentType(),
                experimentRequestDto.getEvaluationMethod());
        var experimentRequestModel = experimentMapper.map(experimentRequestDto);
        experimentRequestModel.setRequestId(requestId);
        experimentRequestModel.setChannel(Channel.WEB.name());
        experimentRequestModel.setCreatedBy(userService.getCurrentUser());
        experimentProcessManager.createExperimentRequest(experimentRequestModel);
        var experiment = experimentRepository.findByRequestId(requestId)
                .orElseThrow(() -> new EntityNotFoundException(Experiment.class, requestId));
        log.info("Experiment request [{}] has been created for instances uuid [{}].", requestId,
                experimentRequestDto.getInstancesUuid());
        return CreateExperimentResultDto.builder()
                .id(experiment.getId())
                .requestId(experiment.getRequestId())
                .build();
    }

    /**
     * Cancel experiment request.
     *
     * @param experiment - experiment id
     */
    @Audit(value = CANCEL_EXPERIMENT_REQUEST, correlationIdKey = "#experiment.requestId")
    public void cancelExperiment(Experiment experiment) {
        log.info("Starting to cancel experiment [{}]", experiment.getRequestId());
        experimentProcessManager.cancelExperiment(experiment);
        log.info("Experiment [{}] has been canceled", experiment.getRequestId());
    }
}
