package com.ecaservice.server.service.evaluation;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.AssertionUtils;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.configuation.ExecutorConfiguration;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.server.mapping.ClassifierOptionsRequestMapperImpl;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapperImpl;
import com.ecaservice.server.mapping.ClassifierReportMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ErsResponseStatusMapperImpl;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.EvaluationRequestMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.repository.ErsRequestRepository;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.InstancesProvider;
import com.ecaservice.server.service.data.InstancesLoaderService;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import com.ecaservice.server.service.ers.ErsClient;
import com.ecaservice.server.service.ers.ErsErrorHandler;
import com.ecaservice.server.service.ers.ErsRequestSender;
import com.ecaservice.server.service.ers.ErsRequestService;
import com.ecaservice.server.service.evaluation.initializers.ClassifierInitializerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.EvaluationMethod;
import eca.ensemble.forests.DecisionTreeType;
import feign.FeignException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.NUM_FOLDS;
import static com.ecaservice.server.TestHelperUtils.NUM_TESTS;
import static com.ecaservice.server.TestHelperUtils.SEED;
import static com.ecaservice.server.TestHelperUtils.createInstancesInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link OptimalClassifierOptionsFetcher} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExecutorConfiguration.class, ClassifiersOptionsAutoConfiguration.class, AppProperties.class,
        CrossValidationConfig.class, EvaluationRequestService.class, InstancesInfoMapperImpl.class,
        ClassifierOptionsRequestModelMapperImpl.class, ClassifierReportMapperImpl.class,
        EvaluationRequestMapperImpl.class, ClassifierOptionsRequestMapperImpl.class, EvaluationLogService.class,
        EvaluationLogMapperImpl.class, ClassifiersProperties.class,
        EvaluationService.class, ErsResponseStatusMapperImpl.class, OptimalClassifierOptionsFetcherImpl.class,
        InstancesInfoMapperImpl.class, ErsRequestService.class, InstancesInfoService.class, InstancesProvider.class,
        ClassifierInfoMapperImpl.class, ErsErrorHandler.class, DateTimeConverter.class})
class OptimalClassifierOptionsFetcherTest extends AbstractJpaTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MODEL_DOWNLOAD_URL = "http//:localhost/model";

    @MockBean
    private ErsClient ersClient;
    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private ErsRequestSender ersRequestSender;
    @MockBean
    private EvaluationResultsService evaluationResultsService;
    @MockBean
    private ClassifierInitializerService classifierInitializerService;
    @MockBean
    private ObjectStorageService objectStorageService;
    @MockBean
    private InstancesMetaDataService instancesMetaDataService;
    @MockBean
    private InstancesLoaderService instancesLoaderService;
    @Autowired
    private ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    @Autowired
    private ErsRequestRepository ersRequestRepository;
    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;
    @Autowired
    private AttributesInfoRepository attributesInfoRepository;
    @Autowired
    private OptimalClassifierOptionsFetcher optimalClassifierOptionsFetcher;

    private InstancesRequestDataModel instancesRequestDataModel;

    private String decisionTreeOptions;

    private String dataUuid;
    private Instances data;

    @Override
    public void init() throws Exception {
        initInstancesData();
        mockLoadInstances();
        DecisionTreeOptions treeOptions = TestHelperUtils.createDecisionTreeOptions();
        treeOptions.setDecisionTreeType(DecisionTreeType.CART);
        decisionTreeOptions = objectMapper.writeValueAsString(treeOptions);
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenReturn(MODEL_DOWNLOAD_URL);
    }

    @Override
    public void deleteAll() {
        ersRequestRepository.deleteAll();
        evaluationLogRepository.deleteAll();
        attributesInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testServiceUnavailable() {
        FeignException.ServiceUnavailable serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        internalTestErrorStatus(serviceUnavailable, ErsResponseStatus.SERVICE_UNAVAILABLE,
                ErrorCode.SERVICE_UNAVAILABLE);
    }

    @Test
    void testErrorStatus() {
        FeignException.BadRequest badRequest = mock(FeignException.BadRequest.class);
        internalTestErrorStatus(badRequest, ErsResponseStatus.ERROR, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    void testsInvalidClassifierOptions() {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(StringUtils.EMPTY)));
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        var optimalClassifierOptions =
                optimalClassifierOptionsFetcher.getOptimalClassifierOptions(instancesRequestDataModel);
        assertThat(optimalClassifierOptions).isNotNull();
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.hasOneElement(optionsRequests);
        ClassifierOptionsRequestModel requestModel = optionsRequests.iterator().next();
        assertThat(requestModel.getResponseStatus()).isEqualTo(ErsResponseStatus.ERROR);
    }

    @Test
    void testGetClassifierOptionsSuccess() {
        ClassifierOptionsResponse response = TestHelperUtils.createClassifierOptionsResponse(Collections
                .singletonList(TestHelperUtils.createClassifierReport(decisionTreeOptions)));
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenReturn(response);
        var classifierOptions =
                optimalClassifierOptionsFetcher.getOptimalClassifierOptions(instancesRequestDataModel);
        assertSuccessResults(classifierOptions);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.hasOneElement(optionsRequests);
        assertSuccessClassifierOptionsRequestModel(optionsRequests.getFirst());
    }

    private void internalTestErrorStatus(Exception ex, ErsResponseStatus expectedStatus, ErrorCode expectedErrorCode) {
        when(ersClient.getClassifierOptions(any(ClassifierOptionsRequest.class))).thenThrow(ex);
        var classifierOptions =
                optimalClassifierOptionsFetcher.getOptimalClassifierOptions(instancesRequestDataModel);
        assertThat(classifierOptions).isNotNull();
        assertThat(classifierOptions.getErrorCode()).isEqualTo(expectedErrorCode);
        List<ClassifierOptionsRequestModel> optionsRequests = classifierOptionsRequestModelRepository.findAll();
        AssertionUtils.hasOneElement(optionsRequests);
        ClassifierOptionsRequestModel requestModel = optionsRequests.get(0);
        assertThat(requestModel.getResponseStatus()).isEqualTo(expectedStatus);
        assertThat(requestModel.getClassifierOptionsResponseModels()).isNullOrEmpty();
    }

    private void assertSuccessResults(ClassifierOptionsResult classifierOptionsResult) {
        assertThat(classifierOptionsResult).isNotNull();
        assertThat(classifierOptionsResult.isFound()).isTrue();
        assertThat(classifierOptionsResult.getClassifierOptions()).isNotNull();
    }

    private void assertSuccessClassifierOptionsRequestModel(ClassifierOptionsRequestModel requestModel) {
        assertThat(requestModel.getInstancesInfo()).isNotNull();
        assertThat(requestModel.getInstancesInfo().getUuid()).isNotNull();
        assertThat(requestModel.getResponseStatus()).isEqualTo(ErsResponseStatus.SUCCESS);
        assertThat(requestModel.getRequestId()).isNotNull();
        assertThat(requestModel.getNumFolds()).isNotNull();
        assertThat(requestModel.getNumTests()).isNotNull();
        assertThat(requestModel.getSeed()).isNotNull();
        assertThat(requestModel.getEvaluationMethod()).isNotNull();
        assertThat(requestModel.getClassifierOptionsResponseModels()).hasSize(1);
    }

    private void initInstancesData() {
        data = TestHelperUtils.loadInstances();
        dataUuid = UUID.randomUUID().toString();
        InstancesInfo instancesInfo = createInstancesInfo();
        instancesInfo.setUuid(dataUuid);
        instancesInfoRepository.save(instancesInfo);
        instancesRequestDataModel =
                new InstancesRequestDataModel(UUID.randomUUID().toString(), dataUuid, EvaluationMethod.CROSS_VALIDATION,
                        NUM_FOLDS, NUM_TESTS, SEED);
    }

    private void mockLoadInstances() {
        var instancesDataModel =
                new InstancesMetaDataModel(dataUuid, data.relationName(), data.numInstances(), data.numAttributes(),
                        data.numClasses(), data.classAttribute().name(), "instances", Collections.emptyList());
        when(instancesMetaDataService.getInstancesMetaData(dataUuid)).thenReturn(instancesDataModel);
        when(instancesLoaderService.loadInstances(dataUuid)).thenReturn(data);
    }
}
