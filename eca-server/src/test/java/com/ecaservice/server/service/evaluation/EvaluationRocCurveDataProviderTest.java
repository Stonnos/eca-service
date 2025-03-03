package com.ecaservice.server.service.evaluation;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.exception.InvalidClassIndexException;
import com.ecaservice.server.exception.ModelDeletedException;
import com.ecaservice.server.exception.UnexpectedRequestStatusException;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.ModelCacheLoader;
import com.ecaservice.server.service.ModelProvider;
import com.ecaservice.web.dto.model.RocCurveDataDto;
import eca.core.evaluation.EvaluationResults;
import eca.core.model.ClassificationModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.classifiers.AbstractClassifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createEvaluationLog;
import static com.ecaservice.server.TestHelperUtils.createInstancesInfo;
import static com.ecaservice.server.TestHelperUtils.getEvaluationResults;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EvaluationRocCurveDataProvider} class.
 *
 * @author Roman Batygin
 */
@Import({EvaluationRocCurveDataProvider.class, ModelProvider.class, ModelCacheLoader.class, AppProperties.class})
class EvaluationRocCurveDataProviderTest extends AbstractJpaTest {

    private static final int SCALE = 4;
    private static final int CLASS_INDEX = 0;

    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private EvaluationRocCurveDataProvider evaluationRocCurveDataProvider;

    @MockBean
    private ObjectStorageService objectStorageService;

    private EvaluationLog evaluationLog;

    @Override
    public void deleteAll() {
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
    void testCalculateRocCurveData() throws IOException, ClassNotFoundException {
        EvaluationResults evaluationResults = getEvaluationResults();
        ClassificationModel classificationModel = new ClassificationModel();
        classificationModel.setClassifier((AbstractClassifier) evaluationResults.getClassifier());
        classificationModel.setData(evaluationResults.getEvaluation().getData());
        classificationModel.setEvaluation(evaluationResults.getEvaluation());
        when(objectStorageService.getObject(evaluationLog.getModelPath(), Object.class))
                .thenReturn(classificationModel);
        RocCurveDataDto rocCurveDataDto =
                evaluationRocCurveDataProvider.getRocCurveData(evaluationLog.getId(), CLASS_INDEX);
        assertThat(rocCurveDataDto).isNotNull();
        BigDecimal expectedAucValue = BigDecimal.valueOf(evaluationResults.getEvaluation().areaUnderROC(CLASS_INDEX))
                .setScale(SCALE, RoundingMode.HALF_UP);
        assertThat(rocCurveDataDto.getAucValue()).isEqualTo(expectedAucValue);
        assertThat(rocCurveDataDto.getRocCurvePoints()).isNotEmpty();
    }

    @Test
    void testCalculateRocCurveDataWithEntityNotFoundError() {
        assertThrows(EntityNotFoundException.class,
                () -> evaluationRocCurveDataProvider.getRocCurveData(-1L, CLASS_INDEX));
    }

    @Test
    void testCalculateRocCurveDataWithInvalidNegativeClassIndex() {
        assertThrows(InvalidClassIndexException.class,
                () -> evaluationRocCurveDataProvider.getRocCurveData(evaluationLog.getId(), -1));
    }

    @Test
    void testCalculateRocCurveDataWithInvalidClassIndex() {
        assertThrows(InvalidClassIndexException.class,
                () -> evaluationRocCurveDataProvider.getRocCurveData(evaluationLog.getId(),
                        evaluationLog.getInstancesInfo().getNumClasses()));
    }

    @Test
    void testCalculateRocCurveDataWithInvalidRequestStatus() {
        evaluationLog.setRequestStatus(RequestStatus.ERROR);
        evaluationLogRepository.save(evaluationLog);
        assertThrows(UnexpectedRequestStatusException.class,
                () -> evaluationRocCurveDataProvider.getRocCurveData(evaluationLog.getId(), CLASS_INDEX));
    }

    @Test
    void testCalculateRocCurveDataWithDeletedModel() {
        evaluationLog.setDeletedDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
        assertThrows(ModelDeletedException.class,
                () -> evaluationRocCurveDataProvider.getRocCurveData(evaluationLog.getId(), CLASS_INDEX));
    }
}
