package com.ecaservice.server.service.experiment;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.bpm.service.ProcessManager;
import com.ecaservice.server.config.ProcessConfig;
import com.ecaservice.server.model.entity.Experiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;

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
    private final ExperimentProcessService experimentProcessService;

    /**
     * Processes experiment.
     *
     * @param id - experiment id
     */
    @Locked(lockName = "experiment", key = "#id")
    public void processExperiment(Long id) {
        var experiment = experimentDataService.getById(id);
        putMdc(TX_ID, experiment.getRequestId());
        putMdc(EV_REQUEST_ID, experiment.getRequestId());
        if (hasActiveProcess(experiment)) {
            log.warn("Experiment [{}] has active processes, Skipped experiment processing...",
                    experiment.getRequestId());
        } else {
            log.info("Starting experiment [{}] business process. Experiment request status [{}], channel [{}]",
                    experiment.getRequestId(), experiment.getRequestStatus(), experiment.getChannel());
            var experimentProcess = experimentProcessService.createNewProcess(experiment);
            processManager.startProcess(processConfig.getProcessExperimentId(), experimentProcess.getProcessUuid(),
                    Collections.singletonMap(EXPERIMENT_ID, experiment.getId()));
        }
    }

    private boolean hasActiveProcess(Experiment experiment) {
        return experimentProcessService.getActiveProcessesCount(experiment) > 0L;
    }
}
