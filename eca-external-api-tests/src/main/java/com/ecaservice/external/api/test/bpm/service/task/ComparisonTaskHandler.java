package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.test.common.service.TestResultsMatcher;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_RESULTS_MATCHER;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;
import static com.ecaservice.external.api.test.util.CamundaUtils.setVariableSafe;

/**
 * Abstract handler for comparison operations.
 *
 * @author Roman Batygin
 */
@Slf4j
public abstract class ComparisonTaskHandler extends AbstractTaskHandler {

    /**
     * Constructor with parameters.
     *
     * @param taskType - task type
     */
    protected ComparisonTaskHandler(TaskType taskType) {
        super(taskType);
    }

    @Override
    public void handle(DelegateExecution execution) throws Exception {
        log.debug("Compare fields for execution with id [{}], process id [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        TestResultsMatcher matcher = getVariable(execution, TEST_RESULTS_MATCHER, TestResultsMatcher.class);
        compareAndMatchFields(execution, autoTestId, matcher);
        setVariableSafe(execution, TEST_RESULTS_MATCHER, matcher);
        log.debug("Compare fields has been finished for execution with id [{}], process id [{}]", execution.getId(),
                execution.getProcessBusinessKey());
    }

    protected abstract void compareAndMatchFields(DelegateExecution execution,
                                                  Long autoTestId,
                                                  TestResultsMatcher matcher) throws Exception;
}
