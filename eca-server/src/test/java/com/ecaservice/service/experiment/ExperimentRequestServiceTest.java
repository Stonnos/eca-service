package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.event.model.ExperimentCreatedEvent;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks {@link ExperimentRequestService} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
public class ExperimentRequestServiceTest {

    @Mock
    private ExperimentService experimentService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private ExperimentRequestService experimentRequestService;

    @Test
    public void testSuccessCreation() {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setId(1L);
        when(experimentService.createExperiment(experimentRequest)).thenReturn(experiment);
        experiment = experimentRequestService.createExperimentRequest(experimentRequest);
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ExperimentCreatedEvent.class));
        Assertions.assertThat(experiment).isNotNull();
        Assertions.assertThat(experiment.getExperimentStatus()).isEqualTo(RequestStatus.NEW);
    }
}
