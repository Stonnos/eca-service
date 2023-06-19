package com.ecaservice.server.service.experiment;

import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.exception.experiment.ExperimentException;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.experiment.AbstractExperimentRequestData;
import com.ecaservice.server.model.experiment.ExperimentMessageRequestData;
import com.ecaservice.server.model.experiment.ExperimentRequestDataVisitor;
import com.ecaservice.server.model.experiment.ExperimentWebRequestData;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentStepRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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

    private static final String EXPERIMENT_TRAIN_DATA_PATH_FORMAT = "experiment-train-data-%s.model";

    private static final List<ExperimentStepStatus> EXPERIMENT_STEP_STATUSES_TO_PROCESS =
            List.of(ExperimentStepStatus.READY, ExperimentStepStatus.FAILED);

    private final ExperimentRepository experimentRepository;
    private final ExperimentStepRepository experimentStepRepository;
    private final ExperimentMapper experimentMapper;
    private final ExperimentStepProcessor experimentStepProcessor;
    private final ObjectStorageService objectStorageService;
    private final CrossValidationConfig crossValidationConfig;
    private final ExperimentProgressService experimentProgressService;

    /**
     * Creates experiment request.
     *
     * @param experimentRequest - experiment request data
     * @return created experiment entity
     */
    public Experiment createExperiment(AbstractExperimentRequestData experimentRequest) {
        log.info("Starting to create experiment [{}] request for data '{}', evaluation method [{}], email '{}'",
                experimentRequest.getExperimentType(), experimentRequest.getData().relationName(),
                experimentRequest.getEvaluationMethod(), experimentRequest.getEmail());
        try {
            Experiment experiment = experimentMapper.map(experimentRequest, crossValidationConfig);
            setAdditionalProperties(experiment, experimentRequest);
            experiment.setRequestStatus(RequestStatus.NEW);
            experiment.setRequestId(experimentRequest.getRequestId());
            String objectPath = String.format(EXPERIMENT_TRAIN_DATA_PATH_FORMAT, experiment.getRequestId());
            objectStorageService.uploadObject(experimentRequest.getData(), objectPath);
            experiment.setTrainingDataPath(objectPath);
            experiment.setCreationDate(LocalDateTime.now());
            experimentRepository.save(experiment);
            log.info("Experiment request [{}] has been created.", experiment.getRequestId());
            return experiment;
        } catch (Exception ex) {
            log.error("There was an error while create experiment request [{}]: {}", experimentRequest.getRequestId(),
                    ex.getMessage());
            throw new ExperimentException(ex.getMessage());
        }
    }

    /**
     * Starts experiment.
     *
     * @param experiment - experiment entity
     */
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
    public void processExperiment(Experiment experiment) {
        log.info("Starting to process experiment [{}]", experiment.getRequestId());
        experimentStepProcessor.processExperimentSteps(experiment);
        log.info("Experiment [{}] has been processed", experiment.getRequestId());
    }

    /**
     * Finishes experiment.
     *
     * @param experiment - experiment entity
     */
    public void finishExperiment(Experiment experiment) {
        log.info("Starting to set experiment [{}] final status", experiment.getRequestId());
        RequestStatus requestStatus = calculateFinalStatus(experiment);
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

    private RequestStatus calculateFinalStatus(Experiment experiment) {
        var stepStatuses = experimentStepRepository.getStepStatuses(experiment);
        if (CollectionUtils.isEmpty(stepStatuses)) {
            throw new ExperimentException(
                    String.format("Got empty steps for experiment [%s]", experiment.getRequestId()));
        }
        if (stepStatuses.stream().anyMatch(EXPERIMENT_STEP_STATUSES_TO_PROCESS::contains)) {
            String error =
                    String.format("Can't calculate experiment [%s] final status. Steps contains one of %s status",
                            experiment.getRequestId(), EXPERIMENT_STEP_STATUSES_TO_PROCESS);
            throw new ExperimentException(error);
        }
        if (stepStatuses.stream().anyMatch(ExperimentStepStatus.ERROR::equals)) {
            return RequestStatus.ERROR;
        }
        if (stepStatuses.stream().anyMatch(ExperimentStepStatus.TIMEOUT::equals)) {
            return RequestStatus.TIMEOUT;
        }
        return RequestStatus.FINISHED;
    }

    private void setAdditionalProperties(Experiment experiment, AbstractExperimentRequestData experimentRequestData) {
        experimentRequestData.visit(new ExperimentRequestDataVisitor() {
            @Override
            public void visit(ExperimentWebRequestData experimentWebRequestData) {
                experiment.setChannel(Channel.WEB);
                experiment.setCreatedBy(experimentWebRequestData.getCreatedBy());
            }

            @Override
            public void visit(ExperimentMessageRequestData experimentMessageRequestData) {
                experiment.setChannel(Channel.QUEUE);
                experiment.setCorrelationId(experimentMessageRequestData.getCorrelationId());
                experiment.setReplyTo(experimentMessageRequestData.getReplyTo());
            }
        });
    }
}
