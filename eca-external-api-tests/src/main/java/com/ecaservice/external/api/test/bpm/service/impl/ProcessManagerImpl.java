package com.ecaservice.external.api.test.bpm.service.impl;

import com.ecaservice.external.api.test.bpm.service.ProcessManager;
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
    public void startProcess(String processId, String versionTag, String processBusinessKey,
                             Map<String, Object> variables) {
        log.debug("Starting process with id [{}], version tag [{}], request id [{}]", processId, versionTag,
                processBusinessKey);
        runtimeService.startProcessInstanceByKey(processId, processBusinessKey, variables);
    }
}
