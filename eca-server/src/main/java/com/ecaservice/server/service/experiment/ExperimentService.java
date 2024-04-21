package com.ecaservice.server.service.experiment;

import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.exception.experiment.ExperimentException;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.model.entity.ChannelVisitor;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.experiment.ExperimentRequestData;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentStepRepository;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Experiment service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentService {

    private static final List<RequestStatus> FINAL_STATUSES =
            List.of(RequestStatus.FINISHED, RequestStatus.ERROR, RequestStatus.TIMEOUT);

    private final ExperimentRepository experimentRepository;
    private final ExperimentStepRepository experimentStepRepository;
    private final ExperimentMapper experimentMapper;
    private final ExperimentStepProcessor experimentStepProcessor;
    private final CrossValidationConfig crossValidationConfig;
    private final ExperimentProgressService experimentProgressService;
    private final InstancesMetaDataService instancesMetaDataService;
    private final InstancesInfoService instancesInfoService;

    /**
     * Creates experiment request.
     *
     * @param experimentRequestData - experiment request data
     * @return created experiment entity
     */
    @NewSpan
    public Experiment createExperiment(ExperimentRequestData experimentRequestData) {
        log.info("Starting to create experiment [{}] request for data uuid [{}], evaluation method [{}], email [{}]",
                experimentRequestData.getExperimentType(), experimentRequestData.getDataUuid(),
                experimentRequestData.getEvaluationMethod(), experimentRequestData.getEmail());
        var instancesMetaDataModel =
                instancesMetaDataService.getInstancesMetaData(experimentRequestData.getDataUuid());
        try {
            Experiment experiment = experimentMapper.map(experimentRequestData, crossValidationConfig);
            var instancesInfo = instancesInfoService.getOrSaveInstancesInfo(instancesMetaDataModel);
            experiment.setInstancesInfo(instancesInfo);
            setAdditionalProperties(experiment, experimentRequestData);
            experiment.setRequestStatus(RequestStatus.NEW);
            experiment.setRequestId(experimentRequestData.getRequestId());
            experiment.setTrainingDataUuid(experimentRequestData.getDataUuid());
            experiment.setChannel(experimentRequestData.getChannel());
            experiment.setCreationDate(LocalDateTime.now());
            experimentRepository.save(experiment);
            log.info("Experiment request [{}] has been created.", experiment.getRequestId());
            return experiment;
        } catch (Exception ex) {
            log.error("There was an error while create experiment request [{}]: {}", experimentRequestData.getRequestId(),
                    ex.getMessage());
            throw new ExperimentException(ex.getMessage());
        }
    }

    /**
     * Starts experiment.
     *
     * @param experiment - experiment entity
     */
    @NewSpan
    @Transactional
    public void startExperiment(Experiment experiment) {
        log.info("Starting to set in progress status for experiment [{}]", experiment.getRequestId());
        experimentProgressService.start(experiment);
        createAndSaveSteps(experiment);
        experiment.setRequestStatus(RequestStatus.IN_PROGRESS);
        experiment.setStartDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        log.info("Experiment [{}] in progress status has been set", experiment.getRequestId());
    }

    /**
     * Processes experiment.
     *
     * @param experiment - experiment entity
     */
    @NewSpan
    public void processExperiment(Experiment experiment) {
        log.info("Starting to process experiment [{}]", experiment.getRequestId());
        experimentStepProcessor.processExperimentSteps(experiment);
        log.info("Experiment [{}] has been processed", experiment.getRequestId());
    }

    /**
     * Finishes experiment.
     *
     * @param experiment    - experiment entity
     * @param requestStatus - final request status (FINISHED, ERROR, TIMEOUT)
     */
    @NewSpan
    public void finishExperiment(Experiment experiment, RequestStatus requestStatus) {
        log.info("Starting to set experiment [{}] final status [{}]", experiment.getRequestId(), requestStatus);
        if (!FINAL_STATUSES.contains(requestStatus)) {
            throw new IllegalArgumentException(
                    String.format("Invalid final request status [%s] for experiment [%s]", requestStatus,
                            experiment.getRequestId()));
        }
        experiment.setRequestStatus(requestStatus);
        experiment.setEndDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        log.info("Final status [{}] has been set for experiment [{}]", requestStatus, experiment.getRequestId());
    }

    private void createAndSaveSteps(Experiment experiment) {
        var steps = Stream.of(ExperimentStep.values())
                .map(experimentStep -> {
                    var experimentStepEntity = new ExperimentStepEntity();
                    experimentStepEntity.setStep(experimentStep);
                    experimentStepEntity.setStepOrder(experimentStep.ordinal());
                    experimentStepEntity.setStatus(ExperimentStepStatus.READY);
                    experimentStepEntity.setExperiment(experiment);
                    experimentStepEntity.setCreated(LocalDateTime.now());
                    return experimentStepEntity;
                })
                .collect(Collectors.toList());
        experimentStepRepository.saveAll(steps);
        var stepNames = steps.stream().map(ExperimentStepEntity::getStep).collect(Collectors.toList());
        log.info("{} steps has been saved for experiment [{}]", stepNames, experiment.getRequestId());
    }

    private void setAdditionalProperties(Experiment experiment, ExperimentRequestData experimentRequestData) {
        experimentRequestData.getChannel().visit(new ChannelVisitor() {
            @Override
            public void visitWeb() {
               experiment.setCreatedBy(experimentRequestData.getCreatedBy());
            }

            @Override
            public void visitQueue() {
                experiment.setCorrelationId(experimentRequestData.getCorrelationId());
                experiment.setReplyTo(experimentRequestData.getReplyTo());
            }
        });
    }
}
