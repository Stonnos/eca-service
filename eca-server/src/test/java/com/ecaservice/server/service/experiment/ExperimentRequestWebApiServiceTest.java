package com.ecaservice.server.service.experiment;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.dto.CreateExperimentRequestDto;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.mapping.DataStorageErrorCodeMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.auth.UsersClient;
import com.ecaservice.server.service.ds.DataStorageService;
import com.ecaservice.user.dto.UserInfoDto;
import eca.core.evaluation.EvaluationMethod;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExperimentRequestWebApiService} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class, AppProperties.class, CrossValidationConfig.class,
        DateTimeConverter.class, InstancesInfoMapperImpl.class, ExperimentService.class,
        ExperimentProgressService.class, ExperimentRequestWebApiService.class, DataStorageErrorCodeMapperImpl.class})
class ExperimentRequestWebApiServiceTest extends AbstractJpaTest {

    private static final String USER = "user";

    @MockBean
    private UserService userService;
    @MockBean
    private UsersClient usersClient;
    @MockBean
    private DataStorageService dataStorageService;
    @MockBean
    private ApplicationEventPublisher eventPublisher;
    @MockBean
    private ObjectStorageService objectStorageService;
    @MockBean
    private FilterService filterService;
    @MockBean
    private ExperimentStepProcessor experimentStepProcessor;

    @Inject
    private ExperimentRepository experimentRepository;

    @Inject
    private ExperimentRequestWebApiService experimentRequestWebApiService;

    private Instances instances;

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
    }

    @Override
    public void init() {
        instances = loadInstances();
    }

    @Test
    void testCreateExperimentRequestSuccess() {
        var experimentRequestDto =
                new CreateExperimentRequestDto(UUID.randomUUID().toString(), ExperimentType.ADA_BOOST,
                        EvaluationMethod.CROSS_VALIDATION);
        when(userService.getCurrentUser()).thenReturn(USER);
        when(usersClient.getUserInfo(USER)).thenReturn(new UserInfoDto());
        when(dataStorageService.getValidInstances(experimentRequestDto.getInstancesUuid())).thenReturn(instances);
        var createExperimentResult = experimentRequestWebApiService.createExperiment(experimentRequestDto);
        assertThat(createExperimentResult).isNotNull();
        assertThat(createExperimentResult.getRequestId()).isNotNull();
        assertThat(createExperimentResult.getId()).isNotNull();
        var experiment = experimentRepository.findById(createExperimentResult.getId()).orElse(null);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getRequestStatus()).isEqualTo(RequestStatus.NEW);
        assertThat(experiment.getRequestId()).isNotNull();
        assertThat(experiment.getCreationDate()).isNotNull();
        assertThat(experiment.getChannel()).isEqualTo(Channel.WEB);
        assertThat(experiment.getTrainingDataPath()).isNotNull();
    }
}
