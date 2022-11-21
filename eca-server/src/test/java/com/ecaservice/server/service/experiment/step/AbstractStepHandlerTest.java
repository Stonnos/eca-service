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
import com.ecaservice.server.service.AbstractJpaTest;
import lombok.Getter;
import org.springframework.util.StopWatch;

import javax.inject.Inject;
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
    @Inject
    private ExperimentProgressRepository experimentProgressRepository;
    @Getter
    @Inject
    private ExperimentStepRepository experimentStepRepository;
    @Getter
    @Inject
    private ExperimentRepository experimentRepository;

    @Override
    public void init() {
        createAndSaveExperimentStep();
    }

    @Override
    public void deleteAll() {
        experimentProgressRepository.deleteAll();
        experimentStepRepository.deleteAll();
        experimentRepository.deleteAll();
    }

    void testStep(BiConsumer<ExperimentContext, ExperimentStepEntity> consumer,
                                  ExperimentStepStatus expectedStatus) {
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
        var experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.IN_PROGRESS);
        experimentRepository.save(experiment);
        experimentStepEntity = TestHelperUtils.createExperimentStepEntity(experiment,
                ExperimentStep.EXPERIMENT_PROCESSING, ExperimentStepStatus.READY);
        experimentStepRepository.save(experimentStepEntity);
    }
}
