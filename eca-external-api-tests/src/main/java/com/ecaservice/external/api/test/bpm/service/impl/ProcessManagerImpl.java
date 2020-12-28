package com.ecaservice.external.api.test.bpm.service.impl;

import com.ecaservice.external.api.test.bpm.service.ProcessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessManagerImpl implements ProcessManager {

    private final RuntimeService runtimeService;

    @Override
    public void startProcess(String processId, String processBusinessKey, Map<String, Object> variables) {
        log.debug("Starting process with id [{}], request id [{}]", processId, processBusinessKey);
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey(processId, processBusinessKey, variables);
        log.debug("Finished process with id [{}], process key [{}]", processInstance.getId(),
                processInstance.getBusinessKey());
    }
}
