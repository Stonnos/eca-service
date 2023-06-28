package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link ClassifiersDataCleaner} class.
 *
 * @author Roman Batygin
 */
@Import({ClassifiersDataCleaner.class, AppProperties.class})
class ClassifierDataCleanerTest extends AbstractJpaTest {

    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;

    @MockBean
    private EvaluationLogService evaluationLogService;

    @Inject
    private AppProperties appProperties;

    @Inject
    private ClassifiersDataCleaner classifiersDataCleaner;

    @Captor
    private ArgumentCaptor<EvaluationLog> argumentCaptor;

    private InstancesInfo instancesInfo;

    @Override
    public void init() {
        instancesInfo = TestHelperUtils.createInstancesInfo();
        instancesInfoRepository.save(instancesInfo);
    }

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
    }

    @Test
    void testRemoveModels() {
        List<EvaluationLog> evaluationLogs = newArrayList();
        evaluationLogs.add(TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                instancesInfo));
        EvaluationLog evaluationLogToRemove =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        evaluationLogToRemove.setEndDate(LocalDateTime.now().minusDays(appProperties.getNumberOfDaysForStorage() + 1));
        evaluationLogs.add(evaluationLogToRemove);
        EvaluationLog timeoutEvaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.TIMEOUT, instancesInfo);
        timeoutEvaluationLog.setDeletedDate(LocalDateTime.now());
        evaluationLogs.add(timeoutEvaluationLog);
        evaluationLogRepository.saveAll(evaluationLogs);
        classifiersDataCleaner.removeModels();
        verify(evaluationLogService, atLeastOnce()).removeModel(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId()).isEqualTo(evaluationLogToRemove.getId());
    }
}
