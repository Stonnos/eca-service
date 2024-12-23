package com.ecaservice.server.service.experiment;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

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

    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @MockBean
    private ExperimentDataService experimentDataService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ExperimentDataCleaner experimentDataCleaner;

    @Captor
    private ArgumentCaptor<Experiment> argumentCaptor;

    private InstancesInfo instancesInfo;

    @Override
    public void init() {
        instancesInfo = TestHelperUtils.createInstancesInfo();
        instancesInfoRepository.save(instancesInfo);
    }

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testRemoveExperimentModels() {
        List<Experiment> experiments = newArrayList();
        experiments.add(
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo));
        Experiment experimentToRemove =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        experimentToRemove.setEndDate(LocalDateTime.now().minusDays(appProperties.getNumberOfDaysForStorage() + 1));
        experimentToRemove.setLockedTtl(LocalDateTime.now().minusSeconds(1L));
        experiments.add(experimentToRemove);
        Experiment finishedExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        experiments.add(finishedExperiment);
        Experiment timeoutExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.TIMEOUT, instancesInfo);
        timeoutExperiment.setDeletedDate(LocalDateTime.now());
        experiments.add(timeoutExperiment);
        Experiment errorExperiment =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.ERROR, instancesInfo);
        experiments.add(errorExperiment);
        Experiment experimentWithBackoff =
                TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED, instancesInfo);
        experimentWithBackoff.setEndDate(LocalDateTime.now().minusDays(appProperties.getNumberOfDaysForStorage() + 1));
        experimentWithBackoff.setLockedTtl(LocalDateTime.now().plusMinutes(1));
        experimentRepository.saveAll(experiments);
        experimentDataCleaner.removeExperimentsModels();
        verify(experimentDataService, atLeastOnce()).removeExperimentModel(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getId()).isEqualTo(experimentToRemove.getId());
    }
}
