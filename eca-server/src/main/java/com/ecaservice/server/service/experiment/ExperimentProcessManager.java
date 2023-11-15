package com.ecaservice.server.service.experiment;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.bpm.model.ExperimentRequestModel;
import com.ecaservice.server.bpm.service.ProcessManager;
import com.ecaservice.server.config.ProcessConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;
import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;
import static com.ecaservice.server.config.lock.ExperimentLockConfiguration.EXPERIMENT_LOCK_REGISTRY;

/**
 * Experiment process manager.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentProcessManager {

    private final ExperimentDataService experimentDataService;
    private final ProcessManager processManager;
    private final ProcessConfig processConfig;
    private final RuntimeService runtimeService;

    /**
     * Processes experiment.
     *
     * @param id - experiment id
     */
    @Locked(lockRegistry = EXPERIMENT_LOCK_REGISTRY, lockName = "experiment", key = "#id", waitForLock = false)
    public void processExperiment(Long id) {
        var experiment = experimentDataService.getById(id);
        if (processManager.hasActiveProcess(experiment.getRequestId())) {
            log.warn("Experiment [{}] has active process. Skipped experiment processing...",
                    experiment.getRequestId());
        } else {
            putMdc(TX_ID, experiment.getRequestId());
            putMdc(EV_REQUEST_ID, experiment.getRequestId());
            log.info("Starting experiment [{}] business process. Experiment request status [{}], channel [{}]",
                    experiment.getRequestId(), experiment.getRequestStatus(), experiment.getChannel());
            Map<String, Object> variables = Collections.singletonMap(EXPERIMENT_ID, experiment.getId());
            processManager.startProcess(processConfig.getProcessExperimentId(), experiment.getRequestId(),
                    variables);
        }
    }

    /**
     * Creates experiment request.
     *
     * @param experimentRequestModel - experiment request data model
     */
    public void createExperimentRequest(ExperimentRequestModel experimentRequestModel) {
        putMdc(TX_ID, experimentRequestModel.getRequestId());
        putMdc(EV_REQUEST_ID, experimentRequestModel.getRequestId());
        log.info("Starting create experiment [{}] request business process",
                experimentRequestModel.getRequestId());
        Map<String, Object> variables = Collections.singletonMap(EVALUATION_REQUEST_DATA, experimentRequestModel);
        processManager.startProcess(processConfig.getCreateExperimentRequestProcessId(),
                experimentRequestModel.getRequestId(), variables);
        log.info("Create experiment [{}] request business process has been finished",
                experimentRequestModel.getRequestId());
    }
}
