package com.ecaservice.server.service.experiment;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.AssertionUtils;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.exception.experiment.ExperimentException;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentProgressRepository;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentStepRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.InstancesInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;


/**
 * Unit tests that checks ExperimentService functionality {@see ExperimentService}.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class, AppProperties.class, CrossValidationConfig.class,
        DateTimeConverter.class, InstancesInfoMapperImpl.class, ExperimentService.class,
        ExperimentProgressService.class, InstancesInfoService.class})
class ExperimentServiceTest extends AbstractJpaTest {

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private ExperimentStepRepository experimentStepRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;
    @Inject
    private ExperimentProgressRepository experimentProgressRepository;
    @MockBean
    private ObjectStorageService objectStorageService;
    @MockBean
    private FilterService filterService;
    @MockBean
    private ExperimentStepProcessor experimentStepProcessor;

    @Inject
    private ExperimentService experimentService;

    @Override
    public void deleteAll() {
        experimentProgressRepository.deleteAll();
        experimentStepRepository.deleteAll();
        experimentRepository.deleteAll();
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
        assertThat(experiment.getTrainingDataPath()).isNotNull();
    }

    @Test
    void testExperimentRequestCreationWithError() throws IOException {
        var experimentMessageRequest = TestHelperUtils.createExperimentMessageRequest();
        doThrow(ObjectStorageException.class)
                .when(objectStorageService)
                .uploadObject(any(Serializable.class), anyString());
        assertThrows(ExperimentException.class, () -> experimentService.createExperiment(experimentMessageRequest));
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
    void testFinishExperimentWith() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL,
                ExperimentStepStatus.COMPLETED);
        internalTestFinishExperiment(experiment, RequestStatus.FINISHED);
    }

    @Test
    void testFinishExperimentWithError() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.ERROR);
        createAndSaveExperimentStep(experiment, ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL,
                ExperimentStepStatus.CANCELED);
        internalTestFinishExperiment(experiment, RequestStatus.ERROR);
    }

    @Test
    void testFinishExperimentWithTimeout() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.COMPLETED);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.TIMEOUT);
        createAndSaveExperimentStep(experiment, ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL,
                ExperimentStepStatus.CANCELED);
        internalTestFinishExperiment(experiment, RequestStatus.TIMEOUT);
    }

    @Test
    void testFinishExperimentWithInvalidStepStatuses() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS);
        createAndSaveExperimentStep(experiment, ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.READY);
        createAndSaveExperimentStep(experiment, ExperimentStep.UPLOAD_EXPERIMENT_MODEL, ExperimentStepStatus.FAILED);
        assertThrows(ExperimentException.class, () -> experimentService.finishExperiment(experiment));
    }

    private Experiment createAndSaveExperiment(RequestStatus requestStatus) {
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), requestStatus);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        return experimentRepository.save(experiment);
    }

    private void createAndSaveExperimentStep(Experiment experiment,
                                             ExperimentStep experimentStep,
                                             ExperimentStepStatus stepStatus) {
        var experimentStepEntity = TestHelperUtils.createExperimentStepEntity(experiment, experimentStep, stepStatus);
        experimentStepRepository.save(experimentStepEntity);
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

    private void internalTestFinishExperiment(Experiment experiment, RequestStatus expectedStatus) {
        experimentService.finishExperiment(experiment);
        var actual = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStatus()).isEqualTo(expectedStatus);
        assertThat(actual.getEndDate()).isNotNull();
    }
}
