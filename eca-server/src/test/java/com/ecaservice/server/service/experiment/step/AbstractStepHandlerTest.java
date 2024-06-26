package com.ecaservice.server.service.experiment.step;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.repository.ExperimentProgressRepository;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentStepRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import java.util.UUID;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Abstract step handler test class.
 *
 * @author Roman Batygin
 */
abstract class AbstractStepHandlerTest extends AbstractJpaTest {

    @Getter
    private ExperimentStepEntity experimentStepEntity;

    @Getter
    @Autowired
    private ExperimentProgressRepository experimentProgressRepository;
    @Getter
    @Autowired
    private ExperimentStepRepository experimentStepRepository;
    @Getter
    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Override
    public void init() {
        createAndSaveExperimentStep();
    }

    @Override
    public void deleteAll() {
        experimentProgressRepository.deleteAll();
        experimentStepRepository.deleteAll();
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    void testStep(BiConsumer<ExperimentContext, ExperimentStepEntity> consumer, ExperimentStepStatus expectedStatus) {
        ExperimentContext context = ExperimentContext.builder()
                .stopWatch(new StopWatch())
                .experiment(experimentStepEntity.getExperiment())
                .build();
        consumer.accept(context, experimentStepEntity);
        verifyStepStatus(expectedStatus);
    }

    private void verifyStepStatus(ExperimentStepStatus expectedStatus) {
        var actualStep = experimentStepRepository.findById(experimentStepEntity.getId()).orElse(null);
        assertThat(actualStep).isNotNull();
        assertThat(actualStep.getStatus()).isEqualTo(expectedStatus);
    }

    private void createAndSaveExperimentStep() {
        var experiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
        experimentStepEntity = TestHelperUtils.createExperimentStepEntity(experiment,
                ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.READY);
        experimentStepRepository.save(experimentStepEntity);
    }
}
