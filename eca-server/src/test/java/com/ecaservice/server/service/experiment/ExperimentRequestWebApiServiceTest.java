package com.ecaservice.server.service.experiment;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.data.storage.dto.ExportInstancesResponseDto;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.dto.CreateExperimentRequestDto;
import com.ecaservice.server.mapping.DataStorageErrorCodeMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.auth.UsersClient;
import com.ecaservice.server.service.data.DataLoaderClient;
import com.ecaservice.server.service.data.InstancesMetaDataService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExperimentRequestWebApiService} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentMapperImpl.class, ExperimentConfig.class, AppProperties.class, CrossValidationConfig.class,
        DateTimeConverter.class, InstancesInfoMapperImpl.class, ExperimentService.class, InstancesInfoService.class,
        ExperimentProgressService.class, ExperimentRequestWebApiService.class, DataStorageErrorCodeMapperImpl.class})
class ExperimentRequestWebApiServiceTest extends AbstractJpaTest {

    private static final String USER = "user";
    private static final String DATA_MD_5_HASH = "3032e188204cb537f69fc7364f638641";

    @MockBean
    private UserService userService;
    @MockBean
    private DataLoaderClient dataLoaderClient;
    @MockBean
    private UsersClient usersClient;
    @MockBean
    private InstancesMetaDataService instancesMetaDataService;
    @MockBean
    private DataStorageService dataStorageService;
    @MockBean
    private ApplicationEventPublisher eventPublisher;
    @MockBean
    private ObjectStorageService objectStorageService;
    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private ExperimentStepProcessor experimentStepProcessor;

    @Inject
    private ExperimentRepository experimentRepository;

    @Inject
    private ExperimentRequestWebApiService experimentRequestWebApiService;

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
    }

    @Override
    public void init() {
       mockLoadInstances();
    }

    @Test
    void testCreateExperimentRequestSuccess() {
        var experimentRequestDto =
                new CreateExperimentRequestDto(UUID.randomUUID().toString(), ExperimentType.ADA_BOOST,
                        EvaluationMethod.CROSS_VALIDATION);
        ExportInstancesResponseDto exportInstancesResponseDto = ExportInstancesResponseDto.builder()
                .externalDataUuid(UUID.randomUUID().toString())
                .build();
        when(userService.getCurrentUser()).thenReturn(USER);
        when(usersClient.getUserInfo(USER)).thenReturn(new UserInfoDto());
        when(dataStorageService.exportValidInstances(experimentRequestDto.getInstancesUuid()))
                .thenReturn(exportInstancesResponseDto);
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
        assertThat(experiment.getTrainingDataUuid()).isNotNull();
        assertThat(experiment.getCreatedBy()).isEqualTo(USER);
    }

    private void mockLoadInstances() {
        Instances data = loadInstances();
        var instancesDataModel = new InstancesMetaDataModel(data.relationName(), data.numInstances(),
                data.numAttributes(), data.numClasses(), data.classAttribute().name(), DATA_MD_5_HASH, "instances");
        when(instancesMetaDataService.getInstancesMetaData(anyString())).thenReturn(instancesDataModel);
    }
}
