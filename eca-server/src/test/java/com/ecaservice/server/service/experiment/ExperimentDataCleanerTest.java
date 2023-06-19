package com.ecaservice.server.service.experiment;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link ExperimentDataCleaner} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentDataCleaner.class, AppProperties.class})
class ExperimentDataCleanerTest extends AbstractJpaTest {

    @Inject
    private ExperimentRepository experimentRepository;

    @MockBean
    private ExperimentDataService experimentDataService;

    @Inject
    private AppProperties appProperties;

    @Inject
    private ExperimentDataCleaner experimentDataCleaner;

    @Captor
    private ArgumentCaptor<Experiment> argumentCaptor;

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
    }

    @Test
    void testRemoveExperimentModels() {
        List<Experiment> experiments = newArrayList();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED));
        Experiment experimentToRemove =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentToRemove.setEndDate(LocalDateTime.now().minusDays(appProperties.getNumberOfDaysForStorage() + 1));
        experiments.add(experimentToRemove);
        Experiment finishedExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experiments.add(finishedExperiment);
        Experiment timeoutExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT);
        timeoutExperiment.setDeletedDate(LocalDateTime.now());
        experiments.add(timeoutExperiment);
        Experiment errorExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR);
        experiments.add(errorExperiment);
        experimentRepository.saveAll(experiments);
        experimentDataCleaner.removeExperimentsModels();
        verify(experimentDataService, atLeastOnce()).removeExperimentModel(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId()).isEqualTo(experimentToRemove.getId());
    }

    @Test
    void testRemoveExperimentTrainingData() {
        List<Experiment> experiments = newArrayList();
        experiments.add(TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED));
        Experiment experimentToRemove =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experimentToRemove.setCreationDate(LocalDateTime.now().minusDays(appProperties.getNumberOfDaysForStorage() + 1));
        experiments.add(experimentToRemove);
        experimentRepository.saveAll(experiments);
        experimentDataCleaner.removeExperimentsTrainingData();
        verify(experimentDataService, atLeastOnce()).removeExperimentTrainingData(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId()).isEqualTo(experimentToRemove.getId());
    }
}
