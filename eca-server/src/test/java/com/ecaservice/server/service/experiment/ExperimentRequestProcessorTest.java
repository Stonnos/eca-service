package com.ecaservice.server.service.experiment;

import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.ExperimentResponseEvent;
import com.ecaservice.server.event.model.push.ExperimentSystemPushEvent;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import javax.inject.Inject;

import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link ExperimentRequestProcessor} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ExperimentRequestProcessorTest extends AbstractJpaTest {

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;

    @Mock
    private ExperimentService experimentService;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private ExperimentRequestProcessor experimentRequestProcessor;

    @Override
    public void init() {
        experimentRequestProcessor =
                new ExperimentRequestProcessor(experimentService, applicationEventPublisher, experimentRepository);
    }

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testStartExperiment() {
        var experiment = createAndSaveExperiment(RequestStatus.NEW, Channel.QUEUE);
        experimentRequestProcessor.startExperiment(experiment.getId());
        verify(experimentService, atLeastOnce()).startExperiment(any(Experiment.class));
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(ExperimentSystemPushEvent.class));
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(ExperimentEmailEvent.class));
    }

    @Test
    void testStartExperimentWithInvalidRequestStatus() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS, Channel.QUEUE);
        experimentRequestProcessor.startExperiment(experiment.getId());
        verify(experimentService, never()).startExperiment(any(Experiment.class));
    }

    @Test
    void testProcessExperiment() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS, Channel.QUEUE);
        experimentRequestProcessor.processExperiment(experiment.getId());
        verify(experimentService, atLeastOnce()).processExperiment(any(Experiment.class));
    }

    @Test
    void testProcessExperimentWithInvalidRequestStatus() {
        var experiment = createAndSaveExperiment(RequestStatus.FINISHED, Channel.QUEUE);
        experimentRequestProcessor.processExperiment(experiment.getId());
        verify(experimentService, never()).processExperiment(any(Experiment.class));
    }

    @Test
    void testFinishExperimentWithQueueChannel() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS, Channel.QUEUE);
        experimentRequestProcessor.finishExperiment(experiment.getId());
        verify(experimentService, atLeastOnce()).finishExperiment(any(Experiment.class));
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(ExperimentResponseEvent.class));
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(ExperimentSystemPushEvent.class));
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(ExperimentEmailEvent.class));
    }

    @Test
    void testFinishExperimentWithWebChannel() {
        var experiment = createAndSaveExperiment(RequestStatus.IN_PROGRESS, Channel.WEB);
        experimentRequestProcessor.finishExperiment(experiment.getId());
        verify(experimentService, atLeastOnce()).finishExperiment(any(Experiment.class));
        verify(applicationEventPublisher, never()).publishEvent(any(ExperimentResponseEvent.class));
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(ExperimentSystemPushEvent.class));
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(ExperimentEmailEvent.class));
    }

    @Test
    void testFinishExperimentWithInvalidRequestStatus() {
        var experiment = createAndSaveExperiment(RequestStatus.FINISHED, Channel.QUEUE);
        experimentRequestProcessor.finishExperiment(experiment.getId());
        verify(experimentService, never()).finishExperiment(any(Experiment.class));
    }

    private Experiment createAndSaveExperiment(RequestStatus requestStatus, Channel channel) {
        var experiment = createExperiment(UUID.randomUUID().toString(), requestStatus);
        experiment.setChannel(channel);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        return experimentRepository.save(experiment);
    }
}
