package com.ecaservice.server.service.experiment;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.bpm.service.ProcessManager;
import com.ecaservice.server.config.ProcessConfig;
import com.ecaservice.server.dto.CreateExperimentRequestDto;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.experiment.ExperimentMessageRequestData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static com.ecaservice.common.web.util.LogHelper.EV_REQUEST_ID;
import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.bpm.CamundaVariables.APP_INSTANCES_UUID;
import static com.ecaservice.server.bpm.CamundaVariables.EXPERIMENT_ID;
import static com.ecaservice.server.bpm.CamundaVariables.EVALUATION_REQUEST_DATA;

/**
 * Experiment process manager.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentProcessManager {

    private final String appInstanceUuid = UUID.randomUUID().toString();

    private final ExperimentDataService experimentDataService;
    private final ProcessManager processManager;
    private final ProcessConfig processConfig;
    private final RuntimeService runtimeService;

    /**
     * Processes experiment.
     *
     * @param id - experiment id
     */
    @Locked(lockName = "experiment", key = "#id")
    public void processExperiment(Long id) {
        var experiment = experimentDataService.getById(id);
        if (hasActiveProcess(experiment)) {
            log.warn("Experiment [{}] has active process. Skipped experiment processing...",
                    experiment.getRequestId());
        } else {
            long activeExperimentProcessesForAppInstance = getActiveExperimentProcessesForAppInstance();
            if (activeExperimentProcessesForAppInstance >= processConfig.getMaxConcurrentExperimentProcesses()) {
                log.warn("Got maximum [{}] active experiment processes. Skipped experiment [{}] processing...",
                        activeExperimentProcessesForAppInstance, experiment.getRequestId());
            } else {
                putMdc(TX_ID, experiment.getRequestId());
                putMdc(EV_REQUEST_ID, experiment.getRequestId());
                log.info("Starting experiment [{}] business process. Experiment request status [{}], channel [{}]",
                        experiment.getRequestId(), experiment.getRequestStatus(), experiment.getChannel());
                Map<String, Object> variables =
                        Map.of(EXPERIMENT_ID, experiment.getId(), APP_INSTANCES_UUID, appInstanceUuid);
                processManager.startProcess(processConfig.getProcessExperimentId(), experiment.getRequestId(),
                        variables);
            }
        }
    }

    /**
     * Creates experiment request.
     *
     * @param experimentMessageRequestData - experiment message request data
     */
    public void createExperimentRequest(ExperimentMessageRequestData experimentMessageRequestData) {
        log.info("Starting create experiment [{}] request business process",
                experimentMessageRequestData.getRequestId());
        Map<String, Object> variables = Collections.singletonMap(EVALUATION_REQUEST_DATA, experimentMessageRequestData);
        processManager.startProcess(processConfig.getCreateExperimentRequestProcessId(),
                experimentMessageRequestData.getRequestId(), variables);
        log.info("Create experiment [{}] request business process has been finished",
                experimentMessageRequestData.getRequestId());
    }

    /**
     * Creates experiment web request.
     *
     * @param requestId            - experiment request id
     * @param experimentRequestDto - experiment request dto
     */
    public void createExperimentWebRequest(String requestId, CreateExperimentRequestDto experimentRequestDto) {
        log.info("Starting create experiment [{}] web request business process", requestId);
        Map<String, Object> variables = Collections.singletonMap(EVALUATION_REQUEST_DATA, experimentRequestDto);
        processManager.startProcess(processConfig.getCreateExperimentWebRequestProcessId(), requestId, variables);
        log.info("Create experiment [{}] business process has been finished", requestId);
    }

    private boolean hasActiveProcess(Experiment experiment) {
        var activeProcessInstancesCount = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(experiment.getRequestId())
                .active()
                .count();
        return activeProcessInstancesCount > 0L;
    }

    private long getActiveExperimentProcessesForAppInstance() {
        return runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processConfig.getProcessExperimentId())
                .variableValueEquals(APP_INSTANCES_UUID, appInstanceUuid)
                .active()
                .count();
    }
}
