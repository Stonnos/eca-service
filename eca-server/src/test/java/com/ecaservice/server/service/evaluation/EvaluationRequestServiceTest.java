package com.ecaservice.server.service.evaluation;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.AssertionUtils;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.configuation.ExecutorConfiguration;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.EvaluationRequestData;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.InstancesProvider;
import com.ecaservice.server.service.data.InstancesLoaderService;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import com.ecaservice.server.service.evaluation.initializers.ClassifierInitializerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static com.ecaservice.server.TestHelperUtils.loadInstances;
import static com.ecaservice.server.util.FieldConstraints.SCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks EvaluationRequestService functionality {@see EvaluationRequestService}.
 *
 * @author Roman Batygin
 */
@Import({ExecutorConfiguration.class, CrossValidationConfig.class, EvaluationLogService.class,
        ClassifiersProperties.class, AppProperties.class, InstancesInfoService.class, InstancesProvider.class,
        EvaluationLogMapperImpl.class, EvaluationService.class, DateTimeConverter.class,
        InstancesInfoMapperImpl.class, ClassifiersOptionsAutoConfiguration.class})
class EvaluationRequestServiceTest extends AbstractJpaTest {

    private static final String MODEL_DOWNLOAD_URL = "http//:localhost/model";

    @MockBean
    private FilterTemplateService filterTemplateService;

    @Autowired
    private CrossValidationConfig crossValidationConfig;
    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;
    @Autowired
    private AttributesInfoRepository attributesInfoRepository;
    @Autowired
    private EvaluationLogMapper evaluationLogMapper;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private ExecutorService executorService;
    @Autowired
    private ClassifiersProperties classifiersProperties;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private InstancesInfoService instancesInfoService;
    @Autowired
    private EvaluationLogService evaluationLogService;

    @Mock
    private ClassifierInitializerService classifierInitializerService;
    @Autowired
    private ClassifierOptionsAdapter classifierOptionsAdapter;
    @Mock
    private ObjectStorageService objectStorageService;
    @MockBean
    private InstancesMetaDataService instancesMetaDataService;
    @Mock
    private InstancesLoaderService instancesLoaderService;

    private EvaluationRequestService evaluationRequestService;

    private final String dataUuid = UUID.randomUUID().toString();

    @Override
    public void init() {
        evaluationRequestService =
                new EvaluationRequestService(appProperties, classifiersProperties, executorService, evaluationService,
                        classifierInitializerService, objectStorageService, instancesLoaderService,
                        evaluationLogService, classifierOptionsAdapter, evaluationLogRepository);
        mockLoadInstances();
    }

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
        attributesInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testSuccessClassification() {
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenReturn(MODEL_DOWNLOAD_URL);
        EvaluationRequestData request = TestHelperUtils.createEvaluationRequestData();
        var evaluationResultsDataModel = createAndProcessEvaluationRequest(request);
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.FINISHED);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.hasOneElement(evaluationLogList);
        var evaluationLog = evaluationLogList.iterator().next();
        assertThat(evaluationLog.getRequestStatus()).isEqualTo(RequestStatus.FINISHED);
        double pctCorrect = evaluationResultsDataModel.getEvaluationResults().getEvaluation().pctCorrect();
        BigDecimal expected = BigDecimal.valueOf(pctCorrect).setScale(SCALE, RoundingMode.HALF_DOWN);
        assertThat(evaluationLog.getPctCorrect().doubleValue()).isEqualTo(expected.doubleValue());
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.FINISHED);
        assertThat(evaluationResultsDataModel.getModelUrl()).isEqualTo(MODEL_DOWNLOAD_URL);
        assertThat(evaluationResultsDataModel.getEvaluationResults()).isNotNull();
        assertThat(evaluationResultsDataModel.getEvaluationResults().getClassifier()).isNotNull();
        assertThat(evaluationResultsDataModel.getEvaluationResults().getEvaluation()).isNotNull();
    }

    @Test
    void testClassificationWithError() {
        EvaluationRequestData request = TestHelperUtils.createEvaluationRequestData();
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenThrow(new RuntimeException());
        var evaluationResultsDataModel = createAndProcessEvaluationRequest(request);
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.ERROR);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.hasOneElement(evaluationLogList);
        assertThat(evaluationLogList.iterator().next().getRequestStatus()).isEqualTo(RequestStatus.ERROR);
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.ERROR);
        assertThat(evaluationResultsDataModel.getEvaluationResults()).isNull();
    }

    private EvaluationResultsDataModel createAndProcessEvaluationRequest(EvaluationRequestData request) {
        var createdEvaluationLog =
                evaluationRequestService.createAndSaveEvaluationRequest(request);
        evaluationRequestService.startEvaluationRequest(createdEvaluationLog);
        return evaluationRequestService.processEvaluationRequest(createdEvaluationLog);
    }

    private void mockLoadInstances() {
        Instances data = loadInstances();
        var instancesDataModel =
                new InstancesMetaDataModel(dataUuid, data.relationName(), data.numInstances(), data.numAttributes(),
                        data.numClasses(), data.classAttribute().name(), "instances", Collections.emptyList());
        when(instancesMetaDataService.getInstancesMetaData(anyString())).thenReturn(instancesDataModel);
        when(instancesLoaderService.loadInstances(anyString())).thenReturn(data);
    }
}
