package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.mapping.EcaResponseMapper;
import com.ecaservice.mapping.EcaResponseMapperImpl;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.async.AsyncTaskService;
import com.ecaservice.service.experiment.mail.NotificationService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks {@link ExperimentRequestService} functionality.
 *
 * @author Roman Batygin
 */
@Import(EcaResponseMapperImpl.class)
public class ExperimentRequestServiceTest extends AbstractJpaTest {

    @Mock
    private ExperimentService experimentService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private AsyncTaskService asyncTaskService;
    @Inject
    private EcaResponseMapper ecaResponseMapper;

    private ExperimentRequestService experimentRequestService;

    @Override
    public void init() {
        experimentRequestService =
                new ExperimentRequestService(experimentService, notificationService, asyncTaskService,
                        ecaResponseMapper);
    }

    @Test
    public void testSuccessCreation() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        when(experimentService.createExperiment(experimentRequest)).thenReturn(experiment);
        doNothing().when(notificationService).notifyByEmail(any(Experiment.class));
        EcaResponse ecaResponse = experimentRequestService.createExperimentRequest(experimentRequest);
        verify(asyncTaskService, atLeastOnce()).perform(any(Runnable.class));
        Assertions.assertThat(ecaResponse).isNotNull();
        Assertions.assertThat(ecaResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
    }
}
