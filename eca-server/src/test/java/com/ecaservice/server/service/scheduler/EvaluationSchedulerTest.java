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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for class {@link EvaluationScheduler}.
 *
 * @author Roman Batygin
 */
@Import(ClassifiersProperties.class)
class EvaluationSchedulerTest extends AbstractJpaTest {

    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private ClassifiersProperties classifiersProperties;

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
                        evaluationLogRepository);
    }

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testProcessNewEvaluationRequests() {
        var newRequest = saveEvaluationLog(Channel.WEB, RequestStatus.NEW);
        saveEvaluationLog(Channel.QUEUE, RequestStatus.NEW);
        saveEvaluationLog(Channel.WEB, RequestStatus.FINISHED);
        saveEvaluationLog(Channel.WEB, RequestStatus.IN_PROGRESS);
        saveEvaluationLog(Channel.WEB, RequestStatus.ERROR);
        evaluationScheduler.processEvaluationRequests();
        verify(evaluationProcessManager, atLeastOnce()).processEvaluationRequest(newRequest.getId());
    }

    private EvaluationLog saveEvaluationLog(Channel channel, RequestStatus requestStatus) {
        var evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), requestStatus);
        evaluationLog.setChannel(channel);
        evaluationLog.setInstancesInfo(instancesInfo);
        return evaluationLogRepository.save(evaluationLog);
    }
}
