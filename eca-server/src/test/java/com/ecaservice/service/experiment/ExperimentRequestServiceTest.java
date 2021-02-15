package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.event.model.ExperimentChangeStatusEvent;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

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
@ExtendWith(MockitoExtension.class)
class ExperimentRequestServiceTest {

    @Mock
    private ExperimentService experimentService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private ExperimentRequestService experimentRequestService;

    @Test
    void testSuccessCreation() {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setId(1L);
        when(experimentService.createExperiment(experimentRequest)).thenReturn(experiment);
        experiment = experimentRequestService.createExperimentRequest(experimentRequest);
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ExperimentChangeStatusEvent.class));
        Assertions.assertThat(experiment).isNotNull();
        Assertions.assertThat(experiment.getRequestStatus()).isEqualTo(RequestStatus.NEW);
    }
}
