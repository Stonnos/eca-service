package com.ecaservice.external.api.test.bpm.service.task;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.test.bpm.model.TaskType;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.test.common.model.MatchResult;
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

    @Override
    public void handle(DelegateExecution execution) throws Exception {
        log.debug("Compare fields for execution with id [{}], process id [{}]", execution.getId(),
                execution.getProcessBusinessKey());
        Long autoTestId = getVariable(execution, AUTO_TEST_ID, Long.class);
        TestResultsMatcher matcher = getVariable(execution, TEST_RESULTS_MATCHER, TestResultsMatcher.class);
        AutoTestEntity autoTestEntity = autoTestRepository.findById(autoTestId)
                .orElseThrow(() -> new EntityNotFoundException(AutoTestEntity.class, autoTestId));
        compareAndMatchFields(execution, autoTestEntity, matcher);
        autoTestRepository.save(autoTestEntity);
        setVariableSafe(execution, TEST_RESULTS_MATCHER, matcher);
        log.debug("Compare fields has been finished for execution with id [{}], process id [{}]", execution.getId(),
                execution.getProcessBusinessKey());
    }

    protected abstract void compareAndMatchFields(DelegateExecution execution,
                                                  AutoTestEntity autoTestEntity,
                                                  TestResultsMatcher matcher) throws Exception;

    protected void compareAndMatchRequestStatus(AutoTestEntity autoTestEntity,
                                                RequestStatus expectedStatus,
                                                RequestStatus actualStatus,
                                                TestResultsMatcher matcher) {
        log.debug("Compare status field for auto test [{}]", autoTestEntity.getId());
        autoTestEntity.setExpectedRequestStatus(expectedStatus);
        autoTestEntity.setActualRequestStatus(actualStatus);
        MatchResult statusMatchResult = matcher.compareAndMatch(expectedStatus, actualStatus);
        autoTestEntity.setRequestStatusMatchResult(statusMatchResult);
        log.debug("Auto test [{}] expected request status [{}], actual request status [{}], match result [{}]",
                autoTestEntity.getId(), expectedStatus, actualStatus, statusMatchResult);
    }
}
