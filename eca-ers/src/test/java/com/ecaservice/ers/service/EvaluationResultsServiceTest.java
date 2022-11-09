package com.ecaservice.ers.service;

import com.ecaservice.ers.AbstractJpaTest;
import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.dto.InstancesReport;
import com.ecaservice.ers.exception.DuplicateRequestIdException;
import com.ecaservice.ers.exception.ResultsNotFoundException;
import com.ecaservice.ers.mapping.ClassificationCostsReportMapperImpl;
import com.ecaservice.ers.mapping.ClassifierOptionsInfoMapperImpl;
import com.ecaservice.ers.mapping.ClassifierReportMapperImpl;
import com.ecaservice.ers.mapping.ConfusionMatrixMapperImpl;
import com.ecaservice.ers.mapping.EvaluationResultsMapperImpl;
import com.ecaservice.ers.mapping.InstancesMapperImpl;
import com.ecaservice.ers.mapping.RocCurveReportMapperImpl;
import com.ecaservice.ers.mapping.StatisticsReportMapperImpl;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.repository.EvaluationResultsInfoRepository;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ecaservice.ers.TestHelperUtils.buildEvaluationResultsReport;
import static com.ecaservice.ers.TestHelperUtils.buildGetEvaluationResultsRequest;
import static com.ecaservice.ers.TestHelperUtils.buildInstancesReport;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link EvaluationResultsService} functionality.
 *
 * @author Roman Batygin
 */
@Import({EvaluationResultsMapperImpl.class, ClassificationCostsReportMapperImpl.class,
        ConfusionMatrixMapperImpl.class, StatisticsReportMapperImpl.class, InstancesMapperImpl.class,
        RocCurveReportMapperImpl.class, InstancesService.class,
        EvaluationResultsService.class, ClassifierReportMapperImpl.class,
        ClassifierOptionsInfoMapperImpl.class})
class EvaluationResultsServiceTest extends AbstractJpaTest {

    private static final int NUM_THREADS = 2;

    @Inject
    private EvaluationResultsService evaluationResultsService;
    @Inject
    private EvaluationResultsInfoRepository evaluationResultsInfoRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;

    @Override
    public void init() {
        evaluationResultsInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Override
    public void deleteAll() {
        evaluationResultsInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testSaveEvaluationResultsReport() {
        EvaluationResultsRequest request = buildEvaluationResultsReport(UUID.randomUUID().toString());
        EvaluationResultsResponse response = evaluationResultsService.saveEvaluationResults(request);
        assertThat(response).isNotNull();
        EvaluationResultsInfo evaluationResultsInfo =
                evaluationResultsInfoRepository.findByRequestId(request.getRequestId());
        assertThat(evaluationResultsInfo).isNotNull();
        assertThat(evaluationResultsInfo.getSaveDate()).isNotNull();
        assertThat(evaluationResultsInfo.getRequestId()).isEqualTo(request.getRequestId());
        assertThat(evaluationResultsInfo.getEvaluationMethod()).isEqualTo(EvaluationMethod.CROSS_VALIDATION);
        assertThat(evaluationResultsInfo.getNumFolds().intValue()).isEqualTo(
                request.getEvaluationMethodReport().getNumFolds().intValue());
        assertThat(evaluationResultsInfo.getNumTests().intValue()).isEqualTo(
                request.getEvaluationMethodReport().getNumTests().intValue());
        assertThat(evaluationResultsInfo.getSeed().intValue()).isEqualTo(
                request.getEvaluationMethodReport().getSeed().intValue());
        assertThat(evaluationResultsInfo.getConfusionMatrix()).isNotNull();
        assertThat(evaluationResultsInfo.getConfusionMatrix()).hasSameSizeAs(request.getConfusionMatrix());
        assertThat(evaluationResultsInfo.getStatistics()).isNotNull();
        assertThat(evaluationResultsInfo.getClassificationCosts()).isNotNull();
        assertThat(evaluationResultsInfo.getClassificationCosts()).hasSameSizeAs(
                request.getClassificationCosts());
        assertThat(evaluationResultsInfo.getClassifierOptionsInfo()).isNotNull();
        assertThat(evaluationResultsInfo.getInstances()).isNotNull();
    }

    @Test
    void testExistingReport() {
        EvaluationResultsRequest request = buildEvaluationResultsReport(UUID.randomUUID().toString());
        evaluationResultsService.saveEvaluationResults(request);
        assertThrows(DuplicateRequestIdException.class, () -> evaluationResultsService.saveEvaluationResults(request));
    }

    @Test
    void testDataCache() {
        EvaluationResultsRequest request = buildEvaluationResultsReport(UUID.randomUUID().toString());
        evaluationResultsService.saveEvaluationResults(request);
        request.setRequestId(UUID.randomUUID().toString());
        EvaluationResultsResponse response = evaluationResultsService.saveEvaluationResults(request);
        assertThat(response).isNotNull();
        assertThat(instancesInfoRepository.count()).isOne();
    }

    @Test
    void testDuplicateRequestIdInMultiThreadEnvironment() throws Exception {
        final String requestId = UUID.randomUUID().toString();
        final CountDownLatch finishedLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    EvaluationResultsRequest request = buildEvaluationResultsReport(requestId);
                    evaluationResultsService.saveEvaluationResults(request);
                } finally {
                    finishedLatch.countDown();
                }
            });
        }
        finishedLatch.await();
        executorService.shutdownNow();
        assertThat(evaluationResultsInfoRepository.count()).isOne();
    }

    @Test
    void testDataCacheIdInMultiThreadEnvironment() throws Exception {
        final CountDownLatch finishedLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        final InstancesReport instancesReport = buildInstancesReport();
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                EvaluationResultsRequest request =
                        buildEvaluationResultsReport(UUID.randomUUID().toString());
                request.setInstances(instancesReport);
                evaluationResultsService.saveEvaluationResults(request);
                finishedLatch.countDown();
            });
        }
        finishedLatch.await();
        executorService.shutdownNow();
        assertThat(evaluationResultsInfoRepository.count()).isEqualTo(2);
        assertThat(instancesInfoRepository.count()).isOne();
    }

    @Test
    void testGetEvaluationResultsNotFound() {
        GetEvaluationResultsRequest request = buildGetEvaluationResultsRequest(UUID.randomUUID().toString());
        assertThrows(ResultsNotFoundException.class,
                () -> evaluationResultsService.getEvaluationResultsResponse(request));
    }

    @Test
    void testGetEvaluationResults() {
        EvaluationResultsRequest evaluationResultsRequest =
                buildEvaluationResultsReport(UUID.randomUUID().toString());
        EvaluationResultsResponse evaluationResultsResponse =
                evaluationResultsService.saveEvaluationResults(evaluationResultsRequest);
        assertThat(evaluationResultsResponse).isNotNull();
        GetEvaluationResultsRequest request =
                buildGetEvaluationResultsRequest(evaluationResultsRequest.getRequestId());
        GetEvaluationResultsResponse response =
                evaluationResultsService.getEvaluationResultsResponse(request);
        assertThat(response).isNotNull();
        assertThat(response.getRequestId()).isEqualTo(request.getRequestId());
        assertThat(response.getClassifierReport()).isNotNull();
        assertThat(response.getEvaluationMethodReport()).isNotNull();
        assertThat(response.getStatistics()).isNotNull();
        assertThat(response.getClassificationCosts()).hasSameSizeAs(
                evaluationResultsRequest.getClassificationCosts());
        assertThat(response.getConfusionMatrix()).hasSameSizeAs(
                evaluationResultsRequest.getConfusionMatrix());
        assertThat(response.getInstances()).isNotNull();
    }
}
