package com.ecaservice.server.service.experiment;

import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.repository.ExperimentStepRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Experiment step service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentStepService {

    private final ExperimentStepRepository experimentStepRepository;

    /**
     * Completes experiment step.
     *
     * @param experimentStepEntity - experiment step entity
     */
    public void complete(ExperimentStepEntity experimentStepEntity) {
        log.info("Starting to complete experiment [{}] step [{}]",
                experimentStepEntity.getExperiment().getRequestId(), experimentStepEntity.getStep());
        updateStatus(experimentStepEntity, ExperimentStepStatus.COMPLETED, null);
        log.info("Experiment [{}] step [{}] has been completed",
                experimentStepEntity.getExperiment().getRequestId(), experimentStepEntity.getStep());
    }

    /**
     * Completes experiment step with error.
     *
     * @param experimentStepEntity - experiment step entity
     * @param errorMessage         - error message
     */
    public void completeWithError(ExperimentStepEntity experimentStepEntity, String errorMessage) {
        log.info("Starting to complete experiment [{}] step [{}] with error",
                experimentStepEntity.getExperiment().getRequestId(), experimentStepEntity.getStep());
        updateStatus(experimentStepEntity, ExperimentStepStatus.ERROR, errorMessage);
        log.info("Experiment [{}] step [{}] has been completed with error",
                experimentStepEntity.getExperiment().getRequestId(), experimentStepEntity.getStep());
    }

    /**
     * Completes experiment step with timaout.
     *
     * @param experimentStepEntity - experiment step entity
     */
    public void timeout(ExperimentStepEntity experimentStepEntity) {
        log.info("Starting to complete experiment [{}] step [{}] with timeout",
                experimentStepEntity.getExperiment().getRequestId(), experimentStepEntity.getStep());
        updateStatus(experimentStepEntity, ExperimentStepStatus.TIMEOUT, null);
        log.info("Experiment [{}] step [{}] has been completed with timeout",
                experimentStepEntity.getExperiment().getRequestId(), experimentStepEntity.getStep());
    }

    /**
     * Fails experiment step.
     *
     * @param experimentStepEntity - experiment step entity
     * @param errorMessage         - error message
     */
    public void failed(ExperimentStepEntity experimentStepEntity, String errorMessage) {
        log.info("Starting to failure experiment [{}] step [{}] with error",
                experimentStepEntity.getExperiment().getRequestId(), experimentStepEntity.getStep());
        updateStatus(experimentStepEntity, ExperimentStepStatus.FAILED, errorMessage);
        log.info("Experiment [{}] step [{}] has been failed",
                experimentStepEntity.getExperiment().getRequestId(), experimentStepEntity.getStep());
    }

    private void updateStatus(ExperimentStepEntity experimentStepEntity,
                              ExperimentStepStatus stepStatus,
                              String errorMessage) {
        experimentStepEntity.setStatus(stepStatus);
        experimentStepEntity.setErrorMessage(errorMessage);
        experimentStepEntity.setCompleted(LocalDateTime.now());
        experimentStepRepository.save(experimentStepEntity);
    }
}
