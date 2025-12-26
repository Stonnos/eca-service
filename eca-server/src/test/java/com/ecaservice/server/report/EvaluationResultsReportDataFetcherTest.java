package com.ecaservice.server.report;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.exception.UnexpectedRequestStatusException;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.report.model.EvaluationResultsReportBean;
import com.ecaservice.server.report.model.EvaluationResultsReportInputData;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.EvaluationResultsAttachmentService;
import com.ecaservice.server.service.ers.ErsRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createEvaluationLog;
import static com.ecaservice.server.TestHelperUtils.createEvaluationResultsRequestEntity;
import static com.ecaservice.server.TestHelperUtils.createInstancesInfo;
import static com.ecaservice.server.TestHelperUtils.loadEvaluationResultsResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EvaluationResultsReportDataFetcher} class.
 *
 * @author Roman Batygin
 */
@Import({SimpleEvaluationResultsReportDataFetcher.class, AppProperties.class})
class EvaluationResultsReportDataFetcherTest extends AbstractJpaTest {

    @MockBean
    private EvaluationResultsReportDataProcessor evaluationResultsReportDataProcessor;
    @MockBean
    private ErsRequestService ersRequestService;
    @MockBean
    private EvaluationResultsAttachmentService evaluationResultsAttachmentService;

    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Autowired
    private EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private EvaluationResultsReportDataFetcher evaluationResultsReportDataFetcher;

    private EvaluationLog evaluationLog;

    @Override
    public void deleteAll() {
        evaluationResultsRequestEntityRepository.deleteAll();
        evaluationLogRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Override
    public void init() {
        InstancesInfo instancesInfo = createInstancesInfo();
        instancesInfoRepository.save(instancesInfo);
        evaluationLog = createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        evaluationLogRepository.save(evaluationLog);
    }

    @Test
    void testGetReportData() {
        evaluationResultsRequestEntityRepository.save(
                createEvaluationResultsRequestEntity(evaluationLog, ErsResponseStatus.SUCCESS)
        );
        var evaluationResultsResponse = loadEvaluationResultsResponse();
        when(ersRequestService.getEvaluationResults(anyString())).thenReturn(evaluationResultsResponse);
        when(evaluationResultsAttachmentService.getAttachment(anyString(),
                any(EvaluationResultsAttachmentType.class))).thenReturn(new byte[0]);
        when(evaluationResultsAttachmentService.getAttachment(anyString(),
                any(EvaluationResultsAttachmentType.class))).thenReturn(new byte[0]);
        when(evaluationResultsReportDataProcessor.processReportData(any(EvaluationResultsReportInputData.class)))
                .thenReturn(new EvaluationResultsReportBean());
        EvaluationResultsReportBean reportData =
                evaluationResultsReportDataFetcher.getReportData(evaluationLog.getId());
        assertThat(reportData).isNotNull();
    }

    @Test
    void testGetReportDataWithEvaluationResultsRequestEntityNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> evaluationResultsReportDataFetcher.getReportData(evaluationLog.getId()));
    }

    @Test
    void testGetReportDataWithErrorEvaluationResultsRequestEntity() {
        evaluationResultsRequestEntityRepository.save(
                createEvaluationResultsRequestEntity(evaluationLog, ErsResponseStatus.ERROR)
        );
        assertThrows(EntityNotFoundException.class,
                () -> evaluationResultsReportDataFetcher.getReportData(evaluationLog.getId()));
    }

    @Test
    void testGetReportDataWithEvaluationLogInErrorStatus() {
        evaluationLog.setRequestStatus(RequestStatus.ERROR);
        evaluationLogRepository.save(evaluationLog);
        assertThrows(UnexpectedRequestStatusException.class,
                () -> evaluationResultsReportDataFetcher.getReportData(evaluationLog.getId()));
    }
}
