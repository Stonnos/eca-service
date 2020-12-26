package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.service.AutoTestService;
import com.ecaservice.external.api.test.service.TestResultsMatcher;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_RESULTS_MATCHER;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

/**
 * Handler to process final test result.
 *
 * @author Roman Batygin
 */
@Component
public class FinalTestResultHandler extends SimpleTaskHandler {

    private final AutoTestService autoTestService;

    /**
     * Constructor with parameters.
     *
     * @param autoTestService - auto test service
     */
    public FinalTestResultHandler(AutoTestService autoTestService) {
        super(TaskType.PROCESS_FINAL_TEST_RESULTS);
        this.autoTestService = autoTestService;
    }

    @Override
    public void internalHandle(DelegateExecution execution) {
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        TestResultsMatcher matcher = getVariable(execution, TEST_RESULTS_MATCHER, TestResultsMatcher.class);
        autoTestService.calculateFinalTestResult(autoTestId, matcher);
    }
}
