package com.ecaservice.server.service.evaluation;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.AssertionUtils;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.configuation.ExecutorConfiguration;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.evaluation.initializers.ClassifierInitializerService;
import eca.core.evaluation.EvaluationMethod;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks EvaluationRequestService functionality {@see EvaluationRequestService}.
 *
 * @author Roman Batygin
 */
@Import({ExecutorConfiguration.class, CrossValidationConfig.class,
        EvaluationLogMapperImpl.class, EvaluationService.class, DateTimeConverter.class,
        InstancesInfoMapperImpl.class, ClassifierInfoMapperImpl.class})
class EvaluationRequestServiceTest extends AbstractJpaTest {

    private static final String MODEL_DOWNLOAD_URL = "http//:localhost/model";

    @Inject
    private CrossValidationConfig crossValidationConfig;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private EvaluationLogMapper evaluationLogMapper;
    @Inject
    private EvaluationService evaluationService;
    @Inject
    private CalculationExecutorService calculationExecutorService;

    @Mock
    private ClassifierInitializerService classifierInitializerService;
    @Mock
    private ClassifierOptionsAdapter classifierOptionsAdapter;
    @Mock
    private ObjectStorageService objectStorageService;

    private EvaluationRequestService evaluationRequestService;

    @Override
    public void init() {
        evaluationRequestService =
                new EvaluationRequestService(crossValidationConfig, calculationExecutorService, evaluationService,
                        evaluationLogRepository, evaluationLogMapper, classifierInitializerService,
                        classifierOptionsAdapter, objectStorageService);
    }

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
    }

    @Test
    void testSuccessClassification() {
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenReturn(MODEL_DOWNLOAD_URL);
        EvaluationRequestDataModel request = TestHelperUtils.createEvaluationRequestData();
        EvaluationResultsDataModel evaluationResultsDataModel = evaluationRequestService.processRequest(request);
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.FINISHED);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.hasOneElement(evaluationLogList);
        var evaluationLog = evaluationLogList.iterator().next();
        assertThat(evaluationLog.getRequestStatus()).isEqualTo(RequestStatus.FINISHED);
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.FINISHED);
        //TODO
       // assertThat(evaluationResponse.getModelUrl()).isEqualTo(MODEL_DOWNLOAD_URL);
        assertThat(evaluationResultsDataModel.getEvaluationResults()).isNotNull();
        assertThat(evaluationResultsDataModel.getEvaluationResults().getClassifier()).isNotNull();
        assertThat(evaluationResultsDataModel.getEvaluationResults().getEvaluation()).isNotNull();
    }

    @Test
    void testClassificationWithException() throws Exception {
        EvaluationRequestDataModel request = TestHelperUtils.createEvaluationRequestData();
        CalculationExecutorServiceImpl executorService = mock(CalculationExecutorServiceImpl.class);
        EvaluationRequestService service =
                new EvaluationRequestService(crossValidationConfig, executorService, evaluationService,
                        evaluationLogRepository, evaluationLogMapper, classifierInitializerService,
                        classifierOptionsAdapter, objectStorageService);
        doThrow(new RuntimeException("Error")).when(executorService)
                .execute(any(), anyLong(), any(TimeUnit.class));
        EvaluationResultsDataModel evaluationResultsDataModel = service.processRequest(request);
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.ERROR);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.hasOneElement(evaluationLogList);
        assertThat(evaluationLogList.iterator().next().getRequestStatus()).isEqualTo(RequestStatus.ERROR);
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.ERROR);
        assertThat(evaluationResultsDataModel.getEvaluationResults()).isNull();
    }

    @Test
    void testClassificationWithError() {
        EvaluationRequestDataModel request = TestHelperUtils.createEvaluationRequestData();
        request.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        request.setNumFolds(1);
        EvaluationResultsDataModel evaluationResultsDataModel = evaluationRequestService.processRequest(request);
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.ERROR);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.hasOneElement(evaluationLogList);
        assertThat(evaluationLogList.iterator().next().getRequestStatus()).isEqualTo(RequestStatus.ERROR);
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.ERROR);
        assertThat(evaluationResultsDataModel.getEvaluationResults()).isNull();
    }

    @Test
    void testTimeoutInClassification() throws Exception {
        EvaluationRequestDataModel request = TestHelperUtils.createEvaluationRequestData();
        CalculationExecutorServiceImpl executorService = mock(CalculationExecutorServiceImpl.class);
        EvaluationRequestService service =
                new EvaluationRequestService(crossValidationConfig, executorService, evaluationService,
                        evaluationLogRepository, evaluationLogMapper, classifierInitializerService,
                        classifierOptionsAdapter, objectStorageService);
        doThrow(TimeoutException.class).when(executorService).execute(any(), anyLong(), any(TimeUnit.class));
        EvaluationResultsDataModel evaluationResultsDataModel = service.processRequest(request);
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.TIMEOUT);
        List<EvaluationLog> evaluationLogList = evaluationLogRepository.findAll();
        AssertionUtils.hasOneElement(evaluationLogList);
        assertThat(evaluationLogList.iterator().next().getRequestStatus()).isEqualTo(RequestStatus.TIMEOUT);
        assertThat(evaluationResultsDataModel.getStatus()).isEqualTo(RequestStatus.TIMEOUT);
        assertThat(evaluationResultsDataModel.getEvaluationResults()).isNull();
    }
}
