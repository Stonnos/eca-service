package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationLog_;
import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.classifiers.ClassifierOptionsInfoProcessor;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.ID3;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ecaservice.server.service.filter.dictionary.FilterDictionaries.CLASSIFIER_NAME;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationLogDataService} functionality.
 *
 * @author Roman Batygin
 */
@Import({AppProperties.class, ClassifierInfoMapperImpl.class, EvaluationLogMapperImpl.class,
        InstancesInfoMapperImpl.class, DateTimeConverter.class})
class EvaluationLogDataServiceTest extends AbstractJpaTest {

    private static final String MODEL_DOWNLOAD_URL = "http://localhost:9000/classifier";

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String INSTANCES_INFO_RELATION_NAME = "instancesInfo.relationName";
    private static final String INSTANCES_INFO_ID = "instancesInfo.id";
    private static final String CLASSIFIER_INFO_CLASSIFIER_NAME = "classifierInfo.classifierName";
    private static final String CART_DESCRIPTION = "Алгоритм CART";
    private static final String C45_DESCRIPTION = "Алгоритм C45";

    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;
    @Inject
    private EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;
    @Inject
    private EntityManager entityManager;
    @Inject
    private AppProperties appProperties;
    @Inject
    private EvaluationLogMapper evaluationLogMapper;

    @Mock
    private FilterTemplateService filterTemplateService;
    @Mock
    private ErsService ersService;
    @Mock
    private ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor;
    @Mock
    private ObjectStorageService objectStorageService;
    private EvaluationLogDataService evaluationLogDataService;

    private InstancesInfo instancesInfo;

    @Override
    public void init() {
        instancesInfo = TestHelperUtils.createInstancesInfo();
        instancesInfoRepository.save(instancesInfo);
        evaluationLogDataService =
                new EvaluationLogDataService(appProperties, filterTemplateService, evaluationLogMapper,
                        classifierOptionsInfoProcessor,
                        ersService, entityManager, objectStorageService, evaluationLogRepository,
                        evaluationResultsRequestEntityRepository);
        evaluationLogDataService.initialize();
    }

    @Override
    public void deleteAll() {
        evaluationResultsRequestEntityRepository.deleteAll();
        evaluationLogRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testRequestsStatusesStatisticsCalculation() {
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.NEW, instancesInfo));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.TIMEOUT,
                        instancesInfo));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS,
                        instancesInfo));
        var requestStatusStatisticsDto = evaluationLogDataService.getRequestStatusesStatistics();
        assertThat(requestStatusStatisticsDto).isNotNull();
        assertThat(requestStatusStatisticsDto.getNewRequestsCount()).isEqualTo(2L);
        assertThat(requestStatusStatisticsDto.getInProgressRequestsCount()).isEqualTo(1L);
        assertThat(requestStatusStatisticsDto.getFinishedRequestsCount()).isEqualTo(1L);
        assertThat(requestStatusStatisticsDto.getErrorRequestsCount()).isEqualTo(3L);
        assertThat(requestStatusStatisticsDto.getTimeoutRequestsCount()).isEqualTo(1L);
        assertThat(requestStatusStatisticsDto.getTotalCount()).isEqualTo(evaluationLogRepository.count());
    }

    /**
     * Tests filter by request status and classifier name order by classifier name.
     */
    @Test
    void testFilterByStatusAndClassifierNameLikeOrderByClassifierName() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        evaluationLog.getClassifierInfo().setClassifierName(CART.class.getSimpleName());
        evaluationLogRepository.save(evaluationLog);
        EvaluationLog evaluationLog1 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        evaluationLog1.getClassifierInfo().setClassifierName(C45.class.getSimpleName());
        evaluationLogRepository.save(evaluationLog1);
        EvaluationLog evaluationLog2 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo);
        evaluationLog2.getClassifierInfo().setClassifierName(CART.class.getSimpleName());
        evaluationLogRepository.save(evaluationLog2);
        EvaluationLog evaluationLog3 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        evaluationLog3.getClassifierInfo().setClassifierName(KNearestNeighbours.class.getSimpleName());
        evaluationLogRepository.save(evaluationLog3);
        EvaluationLog evaluationLog4 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo);
        evaluationLog4.getClassifierInfo().setClassifierName(NeuralNetwork.class.getSimpleName());
        evaluationLogRepository.save(evaluationLog4);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, CLASSIFIER_INFO_CLASSIFIER_NAME, false, null,
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(EvaluationLog_.REQUEST_STATUS,
                Collections.singletonList(RequestStatus.FINISHED.name()), MatchMode.EQUALS));
        pageRequestDto.getFilters().add(
                new FilterRequestDto(CLASSIFIER_INFO_CLASSIFIER_NAME, Collections.singletonList("C"), MatchMode.LIKE));
        Page<EvaluationLog> evaluationLogPage = evaluationLogDataService.getNextPage(pageRequestDto);
        List<EvaluationLog> evaluationLogs = evaluationLogPage.getContent();
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isEqualTo(2);
        assertThat(evaluationLogs).hasSize(2);
        assertThat(evaluationLogs.get(0).getRequestId()).isEqualTo(evaluationLog.getRequestId());
        assertThat(evaluationLogs.get(1).getRequestId()).isEqualTo(evaluationLog1.getRequestId());
    }

    @Test
    void testFilterByInstancesId() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        evaluationLogRepository.save(evaluationLog);
        EvaluationLog evaluationLog1 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        evaluationLog1.setInstancesInfo(TestHelperUtils.createInstancesInfo());
        evaluationLog1.getInstancesInfo().setDataMd5Hash("md5Hash");
        instancesInfoRepository.save(evaluationLog1.getInstancesInfo());
        evaluationLogRepository.save(evaluationLog1);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, CLASSIFIER_INFO_CLASSIFIER_NAME, false, null,
                        newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(INSTANCES_INFO_ID,
                Collections.singletonList(String.valueOf(evaluationLog.getInstancesInfo().getId())), MatchMode.EQUALS));
        Page<EvaluationLog> evaluationLogPage = evaluationLogDataService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isOne();
    }

    /**
     * Tests global filtering by "car" search query and evaluation status equals to FINISHED.
     */
    @Test
    void testGlobalFilter() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        evaluationLog.getClassifierInfo().setClassifierName(CART.class.getSimpleName());
        EvaluationLog evaluationLog1 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo);
        evaluationLog1.getClassifierInfo().setClassifierName(CART.class.getSimpleName());
        EvaluationLog evaluationLog2 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        evaluationLog2.getClassifierInfo().setClassifierName(ID3.class.getSimpleName());
        evaluationLogRepository.saveAll(Arrays.asList(evaluationLog, evaluationLog1, evaluationLog2));
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, EvaluationLog_.CREATION_DATE, false, "car", newArrayList());
        pageRequestDto.getFilters().add(new FilterRequestDto(EvaluationLog_.REQUEST_STATUS,
                Collections.singletonList(RequestStatus.FINISHED.name()), MatchMode.EQUALS));
        when(filterTemplateService.getGlobalFilterFields(FilterTemplateType.EVALUATION_LOG)).thenReturn(
                Arrays.asList(CLASSIFIER_INFO_CLASSIFIER_NAME, EvaluationLog_.REQUEST_ID,
                        INSTANCES_INFO_RELATION_NAME));
        mockClassifiersDictionary();
        var evaluationLogPage = evaluationLogDataService.getEvaluationLogsPage(pageRequestDto);
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalCount()).isOne();
    }

    @Test
    void testGetEvaluationLogDetailsWithInProgressStatus() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS,
                        instancesInfo);
        testGetEvaluationLogDetails(evaluationLog, EvaluationResultsStatus.EVALUATION_IN_PROGRESS);
    }

    @Test
    void testGetEvaluationLogDetailsWithEvaluationErrorStatus() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo);
        testGetEvaluationLogDetails(evaluationLog, EvaluationResultsStatus.EVALUATION_ERROR);
    }

    /**
     * Case 1: There is no one request to ERS
     * Case 2: There is no one ERS request with status SUCCESS
     */
    @Test
    void testGetEvaluationLogDetailsWithNotSentStatus() {
        //Case 1
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        evaluationLogRepository.save(evaluationLog);
        testGetEvaluationLogDetails(evaluationLog, EvaluationResultsStatus.RESULTS_NOT_SENT);
        //Case 2
        EvaluationResultsRequestEntity evaluationResultsRequestEntity = new EvaluationResultsRequestEntity();
        evaluationResultsRequestEntity.setRequestDate(LocalDateTime.now().minusDays(1L));
        evaluationResultsRequestEntity.setRequestId(UUID.randomUUID().toString());
        evaluationResultsRequestEntity.setResponseStatus(ErsResponseStatus.ERROR);
        evaluationResultsRequestEntity.setEvaluationLog(evaluationLog);
        evaluationResultsRequestEntityRepository.save(evaluationResultsRequestEntity);
        testGetEvaluationLogDetails(evaluationLog, EvaluationResultsStatus.RESULTS_NOT_SENT);
    }

    @Test
    void testGetEvaluationResultsNotFound() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        testGetEvaluationLogDetailsWithEvaluationResultsFromErs(evaluationLog,
                EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND);
    }

    @Test
    void testGetEvaluationResultsWithResponseErrorStatus() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        testGetEvaluationLogDetailsWithEvaluationResultsFromErs(evaluationLog, EvaluationResultsStatus.ERROR);
    }

    @Test
    void testGetEvaluationResultsWithServiceUnavailable() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        testGetEvaluationLogDetailsWithEvaluationResultsFromErs(evaluationLog,
                EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE);
    }

    @Test
    void testSuccessGetEvaluationLogDetails() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        testGetEvaluationLogDetailsWithEvaluationResultsFromErs(evaluationLog,
                EvaluationResultsStatus.RESULTS_RECEIVED);
    }

    @Test
    void testGetClassifiersStatisticsData() {
        var evaluationLogs = createAndSaveTestDataForGetClassifiersStatisticsData();
        mockClassifiersDictionary();
        var statisticsData = evaluationLogDataService.getClassifiersStatisticsData(LocalDate.now(), LocalDate.now());
        assertThat(statisticsData).isNotNull();
        assertThat(statisticsData.getTotal()).isEqualTo(evaluationLogs.size());
        verifyChartItem(statisticsData.getDataItems(), CART.class.getSimpleName(), 2L);
        verifyChartItem(statisticsData.getDataItems(), C45.class.getSimpleName(), 1L);
        verifyChartItem(statisticsData.getDataItems(), KNearestNeighbours.class.getSimpleName(), 1L);
        verifyChartItem(statisticsData.getDataItems(), NeuralNetwork.class.getSimpleName(), 0L);
    }

    @Test
    void testGetClassifierContentUrl() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenReturn(MODEL_DOWNLOAD_URL);
        var s3ContentResponseDto = evaluationLogDataService.getModelContentUrl(evaluationLog.getId());
        assertThat(s3ContentResponseDto).isNotNull();
        assertThat(s3ContentResponseDto.getContentUrl()).isEqualTo(MODEL_DOWNLOAD_URL);
    }

    @Test
    void testSuccessRemoveClassifierModel() {
        EvaluationLog evaluationLog = createAndSaveFinishedEvaluationLog();
        evaluationLogDataService.removeModel(evaluationLog);
        EvaluationLog actual = evaluationLogRepository.findById(evaluationLog.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getModelPath()).isNull();
        assertThat(actual.getDeletedDate()).isNotNull();
    }

    private void verifyChartItem(List<ChartDataDto> items, String classifierName, long expectedCount) {
        var chartItem = items.stream()
                .filter(chartDataDto -> chartDataDto.getName().equals(classifierName))
                .findFirst()
                .orElse(null);
        assertThat(chartItem).isNotNull();
        assertThat(chartItem.getCount()).isEqualTo(expectedCount);
    }

    private List<EvaluationLog> createAndSaveTestDataForGetClassifiersStatisticsData() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        evaluationLog.getClassifierInfo().setClassifierName(CART.class.getSimpleName());
        EvaluationLog evaluationLog1 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        evaluationLog1.getClassifierInfo().setClassifierName(C45.class.getSimpleName());
        EvaluationLog evaluationLog2 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo);
        evaluationLog2.getClassifierInfo().setClassifierName(CART.class.getSimpleName());
        EvaluationLog evaluationLog3 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        evaluationLog3.getClassifierInfo().setClassifierName(KNearestNeighbours.class.getSimpleName());
        return evaluationLogRepository.saveAll(List.of(evaluationLog, evaluationLog1, evaluationLog2, evaluationLog3));
    }

    private void testGetEvaluationLogDetails(EvaluationLog evaluationLog, EvaluationResultsStatus expectedStatus) {
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogDataService.getEvaluationLogDetails(evaluationLog);
        Assertions.assertThat(evaluationLogDetailsDto).isNotNull();
        Assertions.assertThat(evaluationLogDetailsDto.getEvaluationResultsDto()).isNotNull();
        Assertions.assertThat(
                evaluationLogDetailsDto.getEvaluationResultsDto().getEvaluationResultsStatus().getValue()).isEqualTo(
                expectedStatus.name());
    }

    private void testGetEvaluationLogDetailsWithEvaluationResultsFromErs(EvaluationLog evaluationLog,
                                                                         EvaluationResultsStatus expectedStatus) {
        EvaluationResultsDto evaluationResultsDto = TestHelperUtils.createEvaluationResultsDto(expectedStatus);
        when(ersService.getEvaluationResultsFromErs(anyString())).thenReturn(evaluationResultsDto);
        testGetEvaluationLogDetails(evaluationLog, expectedStatus);
    }

    private EvaluationLog createAndSaveFinishedEvaluationLog() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED,
                        instancesInfo);
        EvaluationResultsRequestEntity evaluationResultsRequestEntity = new EvaluationResultsRequestEntity();
        evaluationResultsRequestEntity.setRequestDate(LocalDateTime.now().minusDays(1L));
        evaluationResultsRequestEntity.setRequestId(UUID.randomUUID().toString());
        evaluationResultsRequestEntity.setResponseStatus(ErsResponseStatus.SUCCESS);
        evaluationResultsRequestEntity.setEvaluationLog(evaluationLog);
        evaluationLogRepository.save(evaluationLog);
        evaluationResultsRequestEntityRepository.save(evaluationResultsRequestEntity);
        return evaluationLog;
    }

    private void mockClassifiersDictionary() {
        var classifiersDictionary = new FilterDictionaryDto();
        classifiersDictionary.setValues(newArrayList());
        classifiersDictionary.getValues().add(
                new FilterDictionaryValueDto(CART_DESCRIPTION, CART.class.getSimpleName()));
        classifiersDictionary.getValues().add(new FilterDictionaryValueDto(C45_DESCRIPTION, C45.class.getSimpleName()));
        classifiersDictionary.getValues().add(new FilterDictionaryValueDto(KNearestNeighbours.class.getSimpleName(),
                KNearestNeighbours.class.getSimpleName()));
        classifiersDictionary.getValues().add(new FilterDictionaryValueDto(NeuralNetwork.class.getSimpleName(),
                NeuralNetwork.class.getSimpleName()));
        when(filterTemplateService.getFilterDictionary(CLASSIFIER_NAME)).thenReturn(classifiersDictionary);
    }
}
