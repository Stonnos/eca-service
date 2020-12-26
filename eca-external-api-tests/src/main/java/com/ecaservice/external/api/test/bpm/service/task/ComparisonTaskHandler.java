package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.MatchResult;
import com.ecaservice.external.api.test.exception.EntityNotFoundException;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.external.api.test.service.TestResultsMatcher;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import static com.ecaservice.external.api.test.bpm.CamundaVariables.AUTO_TEST_ID;
import static com.ecaservice.external.api.test.bpm.CamundaVariables.TEST_RESULTS_MATCHER;
import static com.ecaservice.external.api.test.util.CamundaUtils.getVariable;

/**
 * Abstract handler for comparison operations.
 *
 * @author Roman Batygin
 */
public abstract class ComparisonTaskHandler extends SimpleTaskHandler {

    private final AutoTestRepository autoTestRepository;

    /**
     * Constructor with parameters.
     *
     * @param taskType           - task type
     * @param autoTestRepository - auto test repository bean
     */
    protected ComparisonTaskHandler(TaskType taskType,
                                    AutoTestRepository autoTestRepository) {
        super(taskType);
        this.autoTestRepository = autoTestRepository;
    }

    //FIXME подумать над сохранением итогового значения totalMatched, totalNotMatched

    @Override
    public void internalHandle(DelegateExecution execution) throws Exception {
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        TestResultsMatcher matcher = getVariable(execution, TEST_RESULTS_MATCHER, TestResultsMatcher.class);
        AutoTestEntity autoTestEntity = autoTestRepository.findById(autoTestId)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestEntity.class, autoTestId));
        compareAndMatchFields(execution, autoTestEntity, matcher);
        autoTestRepository.save(autoTestEntity);
    }

    protected abstract void compareAndMatchFields(DelegateExecution execution,
                                                  AutoTestEntity autoTestEntity,
                                                  TestResultsMatcher matcher) throws Exception;

    protected void compareAndMatchRequestStatus(AutoTestEntity autoTestEntity,
                                                RequestStatus expectedStatus,
                                                RequestStatus actualStatus,
                                                TestResultsMatcher matcher) {
        autoTestEntity.setExpectedRequestStatus(expectedStatus);
        autoTestEntity.setActualRequestStatus(actualStatus);
        MatchResult statusMatchResult = matcher.compareAndMatch(expectedStatus, actualStatus);
        autoTestEntity.setRequestStatusMatchResult(statusMatchResult);
    }
}
