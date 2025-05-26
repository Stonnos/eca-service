package com.ecaservice.server.report;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.exception.UnexpectedRequestStatusException;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.report.model.EvaluationResultsReportBean;
import com.ecaservice.server.report.model.EvaluationResultsReportInputData;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.repository.ExperimentResultsRequestRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.ers.ErsRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static com.ecaservice.server.TestHelperUtils.createExperimentResultsEntity;
import static com.ecaservice.server.TestHelperUtils.createExperimentResultsRequest;
import static com.ecaservice.server.TestHelperUtils.createInstancesInfo;
import static com.ecaservice.server.TestHelperUtils.loadEvaluationResultsResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExperimentResultsReportDataFetcher} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentResultsReportDataFetcher.class})
class ExperimentResultsReportDataFetcherTest extends AbstractJpaTest {

    @MockBean
    private EvaluationResultsReportDataProcessor evaluationResultsReportDataProcessor;
    @MockBean
    private ErsRequestService ersRequestService;

    @Autowired
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;
    @Autowired
    private ExperimentResultsRequestRepository experimentResultsRequestRepository;
    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private ExperimentResultsReportDataFetcher experimentResultsReportDataFetcher;

    private Experiment experiment;
    private ExperimentResultsEntity experimentResultsEntity;

    @Override
    public void deleteAll() {
        experimentResultsRequestRepository.deleteAll();
        experimentResultsEntityRepository.deleteAll();
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Override
    public void init() {
        InstancesInfo instancesInfo = createInstancesInfo();
        instancesInfoRepository.save(instancesInfo);
        experiment = createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        experimentRepository.save(experiment);
        experimentResultsEntity = createExperimentResultsEntity(experiment);
        experimentResultsEntityRepository.save(experimentResultsEntity);
    }

    @Test
    void testGetReportData() {
        experimentResultsRequestRepository.save(
                createExperimentResultsRequest(experimentResultsEntity, ErsResponseStatus.SUCCESS)
        );
        var evaluationResultsResponse = loadEvaluationResultsResponse();
        when(ersRequestService.getEvaluationResults(anyString())).thenReturn(evaluationResultsResponse);
        when(evaluationResultsReportDataProcessor.processReportData(any(EvaluationResultsReportInputData.class)))
                .thenReturn(new EvaluationResultsReportBean());
        EvaluationResultsReportBean reportData =
                experimentResultsReportDataFetcher.getReportData(experimentResultsEntity.getId());
        assertThat(reportData).isNotNull();
    }

    @Test
    void testGetReportDataWithExperimentResultsRequestEntityNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> experimentResultsReportDataFetcher.getReportData(experimentResultsEntity.getId()));
    }

    @Test
    void testGetReportDataWithErrorExperimentResultsRequestEntity() {
        experimentResultsRequestRepository.save(
                createExperimentResultsRequest(experimentResultsEntity, ErsResponseStatus.ERROR)
        );
        assertThrows(EntityNotFoundException.class,
                () -> experimentResultsReportDataFetcher.getReportData(experimentResultsEntity.getId()));
    }

    @Test
    void testGetReportDataWithExperimentInErrorStatus() {
        experiment.setRequestStatus(RequestStatus.ERROR);
        experimentRepository.save(experiment);
        assertThrows(UnexpectedRequestStatusException.class,
                () -> experimentResultsReportDataFetcher.getReportData(experimentResultsEntity.getId()));
    }
}
