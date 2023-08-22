package com.ecaservice.server.service.experiment;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.bpm.service.ProcessManager;
import com.ecaservice.server.config.ProcessConfig;
import com.ecaservice.server.model.entity.Experiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;
import static com.ecaservice.server.config.EcaServiceConfiguration.EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN;

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
    @Locked(lockName = "experiment", key = "#id", lockRegistry = EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN,
            waitForLock = false)
    public void processExperiment(Long id) {
        var experiment = experimentDataService.getById(id);
        if (hasActiveProcess(experiment)) {
            log.warn("Experiment [{}] has active process, Skipped experiment processing...",
                    experiment.getRequestId());
        } else {
            putMdc(TX_ID, experiment.getRequestId());
            putMdc(EV_REQUEST_ID, experiment.getRequestId());
            log.info("Starting experiment [{}] business process. Experiment request status [{}], channel [{}]",
                    experiment.getRequestId(), experiment.getRequestStatus(), experiment.getChannel());
            processManager.startProcess(processConfig.getProcessExperimentId(), experiment.getRequestId(),
                    Collections.singletonMap(EXPERIMENT_ID, experiment.getId()));
        }
    }

    private boolean hasActiveProcess(Experiment experiment) {
        var activeProcessInstances = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(experiment.getRequestId())
                .active()
                .list();
        return activeProcessInstances.size() > 0L;
    }
}
