package com.ecaservice.server.bpm.listeners;

import com.ecaservice.server.service.experiment.ExperimentProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Finish experiment process listener
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FinishExperimentProcessListener implements JavaDelegate {

    private final ExperimentProcessService experimentProcessService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Handles finish experiment process [{}] execution id [{}]", execution.getProcessBusinessKey(),
                execution.getId());
        experimentProcessService.completeProcess(execution.getProcessBusinessKey());
    }
}
