package com.ecaservice.server.bpm.service.impl;

import com.ecaservice.server.bpm.service.ProcessManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessManagerImpl implements ProcessManager {

    private final RuntimeService runtimeService;

    @Override
    public void startProcess(String processId, String processBusinessKey, Map<String, Object> variables) {
        log.info("Starting process with id [{}], process key [{}]", processId, processBusinessKey);
        var processInstance =
                runtimeService.startProcessInstanceByKey(processId, processBusinessKey, variables);
        log.info("Process with id [{}], process key [{}] has been finished", processInstance.getId(),
                processInstance.getBusinessKey());
    }
}
