package com.ecaservice.server.service.ers;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.ExperimentResultsRequest;
import com.ecaservice.server.repository.ErsRequestRepository;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.ers.route.EvaluationResultsRequestPathHandler;
import com.ecaservice.server.service.ers.route.ExperimentResultsRequestPathHandler;
import com.ecaservice.web.dto.model.RoutePathDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createExperimentResultsEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EvaluationResultsRequestPathService} class.
 *
 * @author Roman Batygin
 */
@Import({EvaluationResultsRequestPathService.class, EvaluationResultsRequestPathHandler.class,
        ExperimentResultsRequestPathHandler.class})
class EvaluationResultsRequestPathServiceTest extends AbstractJpaTest {

    @Autowired
    private ErsRequestRepository ersRequestRepository;
    @Autowired
    private EvaluationLogRepository evaluationLogRepository;

    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;

    @Autowired
    private EvaluationResultsRequestPathService evaluationResultsRequestPathService;

    @Override
    public void deleteAll() {
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
        experimentResultsEntityRepository.deleteAll();
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testGetEvaluationResultsPath() {
        EvaluationResultsRequestEntity evaluationResultsRequestEntity = createEvaluationResultsRequestEntity();
        RoutePathDto routePathDto = evaluationResultsRequestPathService.getEvaluationResultsRequestPath(
                evaluationResultsRequestEntity.getRequestId());
        assertThat(routePathDto).isNotNull();
        assertThat(routePathDto.getPath()).isEqualTo(String.format("/dashboard/classifiers/evaluation-results/%d",
                evaluationResultsRequestEntity.getEvaluationLog().getId()));
    }

    @Test
    void testGetExperimentResultsPath() {
        ExperimentResultsRequest experimentResultsRequest = createExperimentResultsRequest();
        RoutePathDto routePathDto = evaluationResultsRequestPathService.getEvaluationResultsRequestPath(
                experimentResultsRequest.getRequestId());
        assertThat(routePathDto).isNotNull();
        assertThat(routePathDto.getPath()).isEqualTo(String.format("/dashboard/experiments/results/details/%d",
                experimentResultsRequest.getExperimentResults().getId()));
    }

    private EvaluationResultsRequestEntity createEvaluationResultsRequestEntity() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        instancesInfoRepository.save(evaluationLog.getInstancesInfo());
        evaluationLogRepository.save(evaluationLog);
        EvaluationResultsRequestEntity evaluationResultsRequestEntity = new EvaluationResultsRequestEntity();
        evaluationResultsRequestEntity.setEvaluationLog(evaluationLog);
        evaluationResultsRequestEntity.setRequestId(UUID.randomUUID().toString());
        evaluationResultsRequestEntity.setResponseStatus(ErsResponseStatus.SUCCESS);
        return ersRequestRepository.save(evaluationResultsRequestEntity);
    }

    private ExperimentResultsRequest createExperimentResultsRequest() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
        ExperimentResultsEntity experimentResultsEntity = createExperimentResultsEntity(experiment);
        experimentResultsEntityRepository.save(experimentResultsEntity);
        ExperimentResultsRequest experimentResultsRequest = new ExperimentResultsRequest();
        experimentResultsRequest.setExperimentResults(experimentResultsEntity);
        experimentResultsRequest.setRequestId(UUID.randomUUID().toString());
        experimentResultsRequest.setResponseStatus(ErsResponseStatus.SUCCESS);
        return ersRequestRepository.save(experimentResultsRequest);
    }
}
