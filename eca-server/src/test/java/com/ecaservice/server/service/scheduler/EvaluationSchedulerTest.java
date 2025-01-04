package com.ecaservice.server.service.scheduler;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.evaluation.ClassifiersDataCleaner;
import com.ecaservice.server.service.evaluation.EvaluationProcessManager;
import com.ecaservice.server.service.evaluation.EvaluationRequestsFetcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.UUID;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for class {@link EvaluationScheduler}.
 *
 * @author Roman Batygin
 */
@Import({ClassifiersProperties.class, EvaluationRequestsFetcher.class})
class EvaluationSchedulerTest extends AbstractJpaTest {

    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private ClassifiersProperties classifiersProperties;

    @Autowired
    private EvaluationRequestsFetcher evaluationRequestsFetcher;

    @MockBean
    private ClassifiersDataCleaner classifiersDataCleaner;
    @MockBean
    private EvaluationProcessManager evaluationProcessManager;

    private EvaluationScheduler evaluationScheduler;

    private InstancesInfo instancesInfo;

    @Override
    public void init() {
        instancesInfo = TestHelperUtils.createInstancesInfo();
        instancesInfoRepository.save(instancesInfo);
        evaluationScheduler =
                new EvaluationScheduler(classifiersProperties, evaluationProcessManager, classifiersDataCleaner,
                        evaluationRequestsFetcher, Executors.newSingleThreadExecutor(), evaluationLogRepository);
    }

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testProcessNewEvaluationRequests() {
        saveEvaluationLog(Channel.WEB, RequestStatus.NEW);
        saveEvaluationLog(Channel.QUEUE, RequestStatus.NEW);
        saveEvaluationLog(Channel.WEB, RequestStatus.FINISHED);
        saveEvaluationLog(Channel.WEB, RequestStatus.IN_PROGRESS);
        saveEvaluationLog(Channel.WEB, RequestStatus.ERROR);
        doAnswer(invocation -> {
            // Mock evaluation request processing, set finished status
            EvaluationLog evaluationLog = invocation.getArgument(0);
            evaluationLog.setRequestStatus(RequestStatus.FINISHED);
            evaluationLogRepository.save(evaluationLog);
            return null;
        }).when(evaluationProcessManager).processEvaluationRequest(any(EvaluationLog.class));
        evaluationScheduler.processEvaluationRequests();
        verify(evaluationProcessManager, atLeastOnce()).processEvaluationRequest(any(EvaluationLog.class));
    }

    private void saveEvaluationLog(Channel channel, RequestStatus requestStatus) {
        var evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), requestStatus);
        evaluationLog.setChannel(channel);
        evaluationLog.setInstancesInfo(instancesInfo);
        evaluationLogRepository.save(evaluationLog);
    }
}
