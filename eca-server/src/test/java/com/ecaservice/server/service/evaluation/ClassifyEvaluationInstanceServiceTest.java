package com.ecaservice.server.service.evaluation;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.exception.ClassifyInstanceInvalidRequestException;
import com.ecaservice.server.exception.ModelDeletedException;
import com.ecaservice.server.exception.UnexpectedRequestStatusException;
import com.ecaservice.server.model.entity.AttributesInfoEntity;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.ClassifyInstanceValidator;
import com.ecaservice.server.service.ModelCacheLoader;
import com.ecaservice.server.service.ModelProvider;
import com.ecaservice.web.dto.model.ClassifyInstanceRequestDto;
import com.ecaservice.web.dto.model.ClassifyInstanceResultDto;
import com.ecaservice.web.dto.model.ClassifyInstanceValueDto;
import eca.core.evaluation.EvaluationResults;
import eca.core.model.ClassificationModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.ecaservice.server.TestHelperUtils.buildClassifyInstanceRequestDto;
import static com.ecaservice.server.TestHelperUtils.createAttributes;
import static com.ecaservice.server.TestHelperUtils.createEvaluationLog;
import static com.ecaservice.server.TestHelperUtils.createInstancesInfo;
import static com.ecaservice.server.TestHelperUtils.getEvaluationResults;
import static com.ecaservice.server.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EvaluationRocCurveDataProvider} class.
 *
 * @author Roman Batygin
 */
@Import({ClassifyEvaluationInstanceService.class, ModelProvider.class,
        ModelCacheLoader.class, AppProperties.class, ClassifyInstanceValidator.class})
class ClassifyEvaluationInstanceServiceTest extends AbstractJpaTest {

    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;
    @Autowired
    private AttributesInfoRepository attributesInfoRepository;

    @Autowired
    private ClassifyEvaluationInstanceService classifyEvaluationInstanceService;

    @MockBean
    private ObjectStorageService objectStorageService;

    private EvaluationLog evaluationLog;
    private AttributesInfoEntity attributesInfoEntity;

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
        attributesInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Override
    public void init() {
        InstancesInfo instancesInfo = createInstancesInfo();
        instancesInfoRepository.save(instancesInfo);
        evaluationLog = createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        evaluationLogRepository.save(evaluationLog);
        saveAttributes();
    }

    @Test
    void testClassifyInstance() throws IOException, ClassNotFoundException {
        EvaluationResults evaluationResults = getEvaluationResults();
        ClassificationModel classificationModel = new ClassificationModel();
        classificationModel.setClassifier((AbstractClassifier) evaluationResults.getClassifier());
        classificationModel.setData(evaluationResults.getEvaluation().getData());
        classificationModel.setEvaluation(evaluationResults.getEvaluation());
        when(objectStorageService.getObject(evaluationLog.getModelPath(), Object.class))
                .thenReturn(classificationModel);

        Instances instances = evaluationResults.getEvaluation().getData();
        Instance expectedInstance = instances.firstInstance();
        ClassifyInstanceRequestDto classifyInstanceRequestDto =
                buildClassifyInstanceRequestDto(evaluationLog.getId(), attributesInfoEntity, expectedInstance);
        ClassifyInstanceResultDto classifyInstanceResultDto =
                classifyEvaluationInstanceService.classifyInstance(classifyInstanceRequestDto);
        assertThat(classifyInstanceResultDto).isNotNull();
        assertThat(classifyInstanceResultDto.getClassValue()).isEqualTo(
                expectedInstance.stringValue(instances.classAttribute()));
        assertThat(classifyInstanceResultDto.getClassIndex()).isEqualTo(
                (int) expectedInstance.value(instances.classAttribute()));
    }

    @Test
    void testClassifyInstanceWithInvalidAttributesSize() {
        ClassifyInstanceRequestDto classifyInstanceRequestDto = new ClassifyInstanceRequestDto();
        classifyInstanceRequestDto.setModelId(evaluationLog.getId());
        classifyInstanceRequestDto.setValues(Collections.singletonList(
                new ClassifyInstanceValueDto(0, BigDecimal.TEN)
        ));
        assertThrows(ClassifyInstanceInvalidRequestException.class, () ->
                classifyEvaluationInstanceService.classifyInstance(classifyInstanceRequestDto)
        );
    }

    @Test
    void testClassifyInstanceWithClassAttributeAsInput() {
        Instances instances = loadInstances();
        Instance expectedInstance = instances.firstInstance();
        ClassifyInstanceRequestDto classifyInstanceRequestDto =
                buildClassifyInstanceRequestDto(evaluationLog.getId(), attributesInfoEntity, expectedInstance);
        classifyInstanceRequestDto.getValues().removeLast();
        classifyInstanceRequestDto.getValues().add(
                new ClassifyInstanceValueDto(instances.classIndex(), BigDecimal.ZERO));
        assertThrows(ClassifyInstanceInvalidRequestException.class, () ->
                classifyEvaluationInstanceService.classifyInstance(classifyInstanceRequestDto)
        );
    }

    @Test
    void testClassifyInstanceWithDuplicateAttributes() {
        ClassifyInstanceRequestDto classifyInstanceRequestDto = new ClassifyInstanceRequestDto();
        classifyInstanceRequestDto.setModelId(evaluationLog.getId());
        var values = IntStream.range(0, attributesInfoEntity.getAttributes().size() - 1)
                .mapToObj(i -> new ClassifyInstanceValueDto(0, BigDecimal.ZERO))
                .toList();
        classifyInstanceRequestDto.setValues(values);
        assertThrows(ClassifyInstanceInvalidRequestException.class, () ->
                classifyEvaluationInstanceService.classifyInstance(classifyInstanceRequestDto)
        );
    }

    @Test
    void testClassifyInstanceWithEntityNotFoundError() {
        assertThrows(EntityNotFoundException.class, () ->
                classifyEvaluationInstanceService.classifyInstance(
                        new ClassifyInstanceRequestDto(-1L, Collections.emptyList()))
        );
    }


    @Test
    void testClassifyInstanceWithInvalidRequestStatus() {
        evaluationLog.setRequestStatus(RequestStatus.ERROR);
        evaluationLogRepository.save(evaluationLog);
        assertThrows(UnexpectedRequestStatusException.class, () ->
                classifyEvaluationInstanceService.classifyInstance(
                        new ClassifyInstanceRequestDto(evaluationLog.getId(), Collections.emptyList()))
        );
    }

    @Test
    void testClassifyInstanceWithDeletedModel() {
        evaluationLog.setDeletedDate(LocalDateTime.now());
        evaluationLogRepository.save(evaluationLog);
        assertThrows(ModelDeletedException.class, () ->
                classifyEvaluationInstanceService.classifyInstance(
                        new ClassifyInstanceRequestDto(evaluationLog.getId(), Collections.emptyList()))
        );
    }

    private void saveAttributes() {
        attributesInfoEntity = createAttributes(evaluationLog.getInstancesInfo());
        attributesInfoRepository.save(attributesInfoEntity);
    }
}
