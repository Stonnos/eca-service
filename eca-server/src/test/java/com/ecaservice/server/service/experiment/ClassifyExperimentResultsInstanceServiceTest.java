package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.exception.ClassifyInstanceInvalidRequestException;
import com.ecaservice.server.exception.ModelDeletedException;
import com.ecaservice.server.exception.UnexpectedRequestStatusException;
import com.ecaservice.server.model.entity.AttributesInfoEntity;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.ClassifyInstanceValidator;
import com.ecaservice.server.service.ModelCacheLoader;
import com.ecaservice.server.service.ModelProvider;
import com.ecaservice.web.dto.model.ClassifyInstanceRequestDto;
import com.ecaservice.web.dto.model.ClassifyInstanceResultDto;
import com.ecaservice.web.dto.model.ClassifyInstanceValueDto;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.core.Instance;
import weka.core.Instances;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.ecaservice.server.TestHelperUtils.buildClassifyInstanceRequestDto;
import static com.ecaservice.server.TestHelperUtils.buildExperiment;
import static com.ecaservice.server.TestHelperUtils.createAttributes;
import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static com.ecaservice.server.TestHelperUtils.createExperimentResultsEntity;
import static com.ecaservice.server.TestHelperUtils.createInstancesInfo;
import static com.ecaservice.server.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ClassifyExperimentResultsInstanceService} class.
 *
 * @author Roman Batygin
 */
@Import({ClassifyExperimentResultsInstanceService.class, ModelProvider.class,
        ModelCacheLoader.class, AppProperties.class, ClassifyInstanceValidator.class})
class ClassifyExperimentResultsInstanceServiceTest extends AbstractJpaTest {

    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;
    @Autowired
    private AttributesInfoRepository attributesInfoRepository;

    @Autowired
    private ClassifyExperimentResultsInstanceService classifyExperimentResultsInstanceService;

    @MockBean
    private ObjectStorageService objectStorageService;

    private Experiment experiment;

    private ExperimentResultsEntity experimentResultsEntity;
    private AttributesInfoEntity attributesInfoEntity;

    @Override
    public void deleteAll() {
        experimentResultsEntityRepository.deleteAll();
        experimentRepository.deleteAll();
        attributesInfoRepository.deleteAll();
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
        saveAttributes();
    }

    @Test
    void testClassifyInstance() throws IOException, ClassNotFoundException {
        AbstractExperiment<?> abstractExperiment = buildExperiment();
        EvaluationResults evaluationResults = abstractExperiment.getHistory().getFirst();
        when(objectStorageService.getObject(experimentResultsEntity.getExperiment().getModelPath(), Object.class))
                .thenReturn(abstractExperiment);

        Instances instances = evaluationResults.getEvaluation().getData();
        Instance expectedInstance = instances.firstInstance();
        ClassifyInstanceRequestDto classifyInstanceRequestDto =
                buildClassifyInstanceRequestDto(experimentResultsEntity.getId(), attributesInfoEntity, expectedInstance);
        ClassifyInstanceResultDto classifyInstanceResultDto =
                classifyExperimentResultsInstanceService.classifyInstance(classifyInstanceRequestDto);
        assertThat(classifyInstanceResultDto).isNotNull();
        assertThat(classifyInstanceResultDto.getClassValue()).isEqualTo(
                expectedInstance.stringValue(instances.classAttribute()));
        assertThat(classifyInstanceResultDto.getClassIndex()).isEqualTo(
                (int) expectedInstance.value(instances.classAttribute()));
    }

    @Test
    void testClassifyInstanceWithInvalidAttributesSize() {
        ClassifyInstanceRequestDto classifyInstanceRequestDto = new ClassifyInstanceRequestDto();
        classifyInstanceRequestDto.setModelId(experimentResultsEntity.getId());
        classifyInstanceRequestDto.setValues(Collections.singletonList(
                new ClassifyInstanceValueDto(0, BigDecimal.TEN)
        ));
        assertThrows(ClassifyInstanceInvalidRequestException.class, () ->
                classifyExperimentResultsInstanceService.classifyInstance(classifyInstanceRequestDto)
        );
    }

    @Test
    void testClassifyInstanceWithClassAttributeAsInput() {
        Instances instances = loadInstances();
        Instance expectedInstance = instances.firstInstance();
        ClassifyInstanceRequestDto classifyInstanceRequestDto =
                buildClassifyInstanceRequestDto(experimentResultsEntity.getId(), attributesInfoEntity, expectedInstance);
        classifyInstanceRequestDto.getValues().removeLast();
        classifyInstanceRequestDto.getValues().add(
                new ClassifyInstanceValueDto(instances.classIndex(), BigDecimal.ZERO));
        assertThrows(ClassifyInstanceInvalidRequestException.class, () ->
                classifyExperimentResultsInstanceService.classifyInstance(classifyInstanceRequestDto)
        );
    }

    @Test
    void testClassifyInstanceWithDuplicateAttributes() {
        ClassifyInstanceRequestDto classifyInstanceRequestDto = new ClassifyInstanceRequestDto();
        classifyInstanceRequestDto.setModelId(experimentResultsEntity.getId());
        var values = IntStream.range(0, attributesInfoEntity.getAttributes().size() - 1)
                .mapToObj(i -> new ClassifyInstanceValueDto(0, BigDecimal.ZERO))
                .toList();
        classifyInstanceRequestDto.setValues(values);
        assertThrows(ClassifyInstanceInvalidRequestException.class, () ->
                classifyExperimentResultsInstanceService.classifyInstance(classifyInstanceRequestDto)
        );
    }

    @Test
    void testClassifyInstanceWithEntityNotFoundError() {
        assertThrows(EntityNotFoundException.class, () ->
                classifyExperimentResultsInstanceService.classifyInstance(
                        new ClassifyInstanceRequestDto(-1L, Collections.emptyList()))
        );
    }


    @Test
    void testClassifyInstanceWithInvalidRequestStatus() {
        experiment.setRequestStatus(RequestStatus.ERROR);
        experimentRepository.save(experiment);
        assertThrows(UnexpectedRequestStatusException.class, () ->
                classifyExperimentResultsInstanceService.classifyInstance(
                        new ClassifyInstanceRequestDto(experimentResultsEntity.getId(), Collections.emptyList()))
        );
    }

    @Test
    void testClassifyInstanceWithDeletedModel() {
        experiment.setDeletedDate(LocalDateTime.now());
        experimentRepository.save(experiment);
        assertThrows(ModelDeletedException.class, () ->
                classifyExperimentResultsInstanceService.classifyInstance(
                        new ClassifyInstanceRequestDto(experimentResultsEntity.getId(), Collections.emptyList()))
        );
    }

    private void saveAttributes() {
        attributesInfoEntity = createAttributes(experiment.getInstancesInfo());
        attributesInfoRepository.save(attributesInfoEntity);
    }
}
