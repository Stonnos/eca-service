package com.ecaservice.service.experiment;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.mapping.EcaResponseMapper;
import com.ecaservice.mapping.EcaResponseMapperImpl;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.async.AsyncTaskCallback;
import com.ecaservice.service.async.AsyncTaskService;
import com.ecaservice.service.experiment.mail.NotificationService;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

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
@RunWith(SpringRunner.class)
@AutoConfigureDataJpa
@EnableJpaRepositories(basePackageClasses = ExperimentRepository.class)
@EntityScan(basePackageClasses = Experiment.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import(EcaResponseMapperImpl.class)
public class ExperimentRequestServiceTest {

    @Mock
    private ExperimentService experimentService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private AsyncTaskService asyncTaskService;
    @Inject
    private EcaResponseMapper ecaResponseMapper;

    private ExperimentRequestService experimentRequestService;

    @Before
    public void init() {
        experimentRequestService =
                new ExperimentRequestService(experimentService, notificationService, asyncTaskService,
                        ecaResponseMapper);
    }

    @Test
    public void testInvalidEmail() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        experimentRequest.setEmail("#443@de.cdcd32");
        EcaResponse ecaResponse = experimentRequestService.createExperimentRequest(experimentRequest);
        Assertions.assertThat(ecaResponse).isNotNull();
        Assertions.assertThat(ecaResponse.getStatus()).isEqualTo(TechnicalStatus.ERROR);
    }

    @Test
    public void testSuccessCreation() throws Exception {
        ExperimentRequest experimentRequest = TestHelperUtils.createExperimentRequest();
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        when(experimentService.createExperiment(experimentRequest)).thenReturn(experiment);
        doNothing().when(notificationService).notifyByEmail(any(Experiment.class));
        EcaResponse ecaResponse = experimentRequestService.createExperimentRequest(experimentRequest);
        verify(asyncTaskService, atLeastOnce()).perform(any(AsyncTaskCallback.class));
        Assertions.assertThat(ecaResponse).isNotNull();
        Assertions.assertThat(ecaResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
    }
}
