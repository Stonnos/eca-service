package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.test.bpm.model.ExecutionResult;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.ExecutionStatus;
import com.ecaservice.external.api.test.entity.TestResult;
import com.ecaservice.external.api.test.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.EXECUTION_RESULT;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

/**
 * Error handler.
 *
 * @author Roman Batygin
 */
@Component
public class ErrorHandler extends SimpleTaskHandler {

    private final AutoTestRepository autoTestRepository;

    /**
     * Constructor with parameters.
     *
     * @param autoTestRepository - auto test repository bean.
     */
    public ErrorHandler(AutoTestRepository autoTestRepository) {
        super(TaskType.FINISH_WITH_ERROR);
        this.autoTestRepository = autoTestRepository;
    }

    @Override
    public void internalHandle(DelegateExecution execution) {
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        ExecutionResult lastErrorExecutionResult = getVariable(execution, EXECUTION_RESULT, ExecutionResult.class);
        AutoTestEntity autoTestEntity = autoTestRepository.findById(autoTestId)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestEntity.class, autoTestId));
        autoTestEntity.setTestResult(TestResult.ERROR);
        autoTestEntity.setExecutionStatus(ExecutionStatus.ERROR);
        autoTestEntity.setDetails(lastErrorExecutionResult.getErrorMessage());
        autoTestRepository.save(autoTestEntity);
    }
}
