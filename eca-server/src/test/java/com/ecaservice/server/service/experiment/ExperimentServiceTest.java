package com.ecaservice.server.service.experiment;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.server.AssertionUtils;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.ExperimentProgressRepository;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentStepRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.data.InstancesLoaderService;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ecaservice.server.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


/**
 * Unit tests that checks ExperimentService functionality {@see ExperimentService}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class, AppProperties.class, CrossValidationConfig.class,
        DateTimeConverter.class, InstancesInfoMapperImpl.class, ExperimentService.class,
        ExperimentProgressService.class, InstancesInfoService.class})
class ExperimentServiceTest extends AbstractJpaTest {

    private static final String DATA_MD_5_HASH = "3032e188204cb537f69fc7364f638641";

    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private ExperimentStepRepository experimentStepRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;
    @Autowired
    private AttributesInfoRepository attributesInfoRepository;
    @Autowired
    private ExperimentProgressRepository experimentProgressRepository;
    @MockBean
    private InstancesMetaDataService instancesMetaDataService;
    @MockBean
    private InstancesLoaderService instancesLoaderService;
    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private ExperimentStepProcessor experimentStepProcessor;

    @Autowired
    private ExperimentService experimentService;

    @Override
    public void init() {
        mockLoadInstances();
    }

    @Override
    public void deleteAll() {
        experimentProgressRepository.deleteAll();
        experimentStepRepository.deleteAll();
        experimentRepository.deleteAll();
        attributesInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testSuccessExperimentRequestCreation() {
        var experimentMessageRequest = TestHelperUtils.createExperimentMessageRequest();
        experimentService.createExperiment(experimentMessageRequest);
        List<Experiment> experiments = experimentRepository.findAll();
        AssertionUtils.hasOneElement(experiments);
        Experiment experiment = experiments.iterator().next();
        assertThat(experiment.getRequestStatus()).isEqualTo(RequestStatus.NEW);
        assertThat(experiment.getRequestId()).isNotNull();
        assertThat(experiment.getCreationDate()).isNotNull();
        assertThat(experiment.getChannel()).isEqualTo(Channel.QUEUE);
        assertThat(experiment.getReplyTo()).isEqualTo(experimentMessageRequest.getReplyTo());
        assertThat(experiment.getCorrelationId()).isEqualTo(experimentMessageRequest.getCorrelationId());
        assertThat(experiment.getTrainingDataUuid()).isNotNull();
    }

    @Test
    void testStartExperiment() {
        var experiment = createAndSaveExperiment(RequestStatus.NEW);
        experimentService.startExperiment(experiment);
        var actual = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
        assertThat(actual.getStartDate()).isNotNull();
        verifySavedSteps(experiment);
        var experimentProgressEntity = experimentProgressRepository.findByExperiment(experiment).orElse(null);
        assertThat(experimentProgressEntity).isNotNull();
        assertThat(experimentProgressEntity.isFinished()).isFalse();
        assertThat(experimentProgressEntity.getProgress()).isZero();
    }

    @Test
    void testFinishExperiment() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS);
        experimentService.finishExperiment(experiment, RequestStatus.FINISHED);
        var actual = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(RequestStatus.FINISHED);
        assertThat(actual.getEndDate()).isNotNull();
    }

    private Experiment createAndSaveExperiment(RequestStatus requestStatus) {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), requestStatus);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        return experimentRepository.save(experiment);
    }

    private void verifySavedSteps(Experiment experiment) {
        var steps = experimentStepRepository.findAll()
                .stream()
                .collect(Collectors.toMap(ExperimentStepEntity::getStep, Function.identity()));
        assertThat(steps).hasSize(ExperimentStep.values().length);
        Stream.of(ExperimentStep.values()).forEach(experimentStep -> {
            var step = steps.get(experimentStep);
            assertThat(step).isNotNull();
            assertThat(step.getStep()).isEqualTo(experimentStep);
            assertThat(step.getStepOrder()).isEqualTo(experimentStep.ordinal());
            assertThat(step.getStatus()).isEqualTo(ExperimentStepStatus.READY);
            assertThat(step.getExperiment()).isNotNull();
            assertThat(step.getExperiment().getId()).isEqualTo(experiment.getId());
        });
    }

    private void mockLoadInstances() {
        Instances data = loadInstances();
        var instancesDataModel =
                new InstancesMetaDataModel(UUID.randomUUID().toString(), data.relationName(), data.numInstances(),
                        data.numAttributes(), data.numClasses(), data.classAttribute().name(), DATA_MD_5_HASH,
                        "instances", Collections.emptyList());
        when(instancesMetaDataService.getInstancesMetaData(anyString())).thenReturn(instancesDataModel);
        when(instancesLoaderService.loadInstances(anyString())).thenReturn(data);
    }
}
