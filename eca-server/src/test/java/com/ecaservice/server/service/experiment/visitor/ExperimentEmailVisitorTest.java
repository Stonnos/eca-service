package com.ecaservice.server.service.experiment.visitor;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.experiment.mail.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;
import java.util.function.Consumer;

import static com.ecaservice.TestHelperUtils.createExperiment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;


/**
 * Unit tests for {@link ExperimentEmailVisitor} class.
 *
 * @author Roman Batygin
 */
@Import(ExperimentEmailVisitor.class)
class ExperimentEmailVisitorTest extends AbstractJpaTest {

    @MockBean
    private NotificationService notificationService;

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private ExperimentEmailVisitor experimentEmailVisitor;

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
    }

    @Test
    void testHandleNewExperiment() {
        internalTestTransitionStatus(RequestStatus.NEW, experimentEmailVisitor::caseNew);
    }

    @Test
    void testHandleInProgressExperiment() {
        internalTestTransitionStatus(RequestStatus.IN_PROGRESS, experimentEmailVisitor::caseInProgress);
    }

    @Test
    void testHandleFinishedExperiment() {
        internalTestFinalStatus(RequestStatus.FINISHED, experimentEmailVisitor::caseFinished);
    }

    @Test
    void testHandleErrorExperiment() {
        internalTestFinalStatus(RequestStatus.ERROR, experimentEmailVisitor::caseError);
    }

    @Test
    void testHandleTimeoutExperiment() {
        internalTestFinalStatus(RequestStatus.TIMEOUT, experimentEmailVisitor::caseTimeout);
    }

    private void internalTestTransitionStatus(RequestStatus requestStatus, Consumer<Experiment> consumer) {
        var experiment = createAndSave(requestStatus);
        consumer.accept(experiment);
        verify(notificationService, atLeastOnce()).notifyByEmail(experiment);
        var actual = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getSentDate()).isNull();
    }

    private void internalTestFinalStatus(RequestStatus requestStatus, Consumer<Experiment> consumer) {
        var experiment = createAndSave(requestStatus);
        consumer.accept(experiment);
        verify(notificationService, atLeastOnce()).notifyByEmail(experiment);
        var actual = experimentRepository.findById(experiment.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getSentDate()).isNotNull();
    }

    private Experiment createAndSave(RequestStatus requestStatus) {
        var experiment = createExperiment(UUID.randomUUID().toString(), requestStatus);
        return experimentRepository.save(experiment);
    }
}
