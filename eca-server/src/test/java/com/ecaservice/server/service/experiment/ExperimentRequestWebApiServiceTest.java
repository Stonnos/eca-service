package com.ecaservice.server.service.experiment;

import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.bpm.model.ExperimentRequestModel;
import com.ecaservice.server.dto.CreateExperimentRequestDto;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.UserService;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import eca.core.evaluation.EvaluationMethod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExperimentRequestWebApiService} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentRequestWebApiService.class, ExperimentMapperImpl.class,
        InstancesInfoMapperImpl.class, DateTimeConverter.class})
class ExperimentRequestWebApiServiceTest extends AbstractJpaTest {

    private static final String CURRENT_USER = "test-user";

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private ExperimentProcessManager experimentProcessManager;

    @Autowired
    private ExperimentRequestWebApiService experimentRequestWebApiService;

    @Override
    public void deleteAll() {
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void testCreateExperimentSuccess() {
        CreateExperimentRequestDto requestDto = new CreateExperimentRequestDto();
        requestDto.setExperimentType(ExperimentType.KNN);
        requestDto.setInstancesUuid(UUID.randomUUID().toString());
        requestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);

        when(userService.getCurrentUser()).thenReturn(CURRENT_USER);

        doAnswer(invocation -> {
            ExperimentRequestModel model = invocation.getArgument(0);
            saveExperiment(model.getRequestId());
            return null;
        }).when(experimentProcessManager).createExperimentRequest(any(ExperimentRequestModel.class));

        CreateExperimentResultDto response = experimentRequestWebApiService.createExperiment(requestDto);

        ArgumentCaptor<ExperimentRequestModel> modelCaptor = ArgumentCaptor.forClass(ExperimentRequestModel.class);
        verify(experimentProcessManager).createExperimentRequest(modelCaptor.capture());

        ExperimentRequestModel capturedModel = modelCaptor.getValue();
        assertThat(capturedModel.getRequestId()).isNotBlank();
        assertThat(UUID.fromString(capturedModel.getRequestId())).isNotNull();
        assertThat(capturedModel.getExperimentType()).isEqualTo(requestDto.getExperimentType());
        assertThat(capturedModel.getChannel()).isEqualTo(Channel.WEB.name());
        assertThat(capturedModel.getCreatedBy()).isEqualTo(CURRENT_USER);
        assertThat(MDC.get(TX_ID)).isEqualTo(capturedModel.getRequestId());

        Experiment savedExperiment = experimentRepository.findByRequestId(capturedModel.getRequestId()).orElseThrow();
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedExperiment.getId());
        assertThat(response.getRequestId()).isEqualTo(capturedModel.getRequestId());
    }

    @Test
    void testCancelExperiment() {
        Experiment experiment = new Experiment();
        experiment.setRequestId(UUID.randomUUID().toString());
        experimentRequestWebApiService.cancelExperiment(experiment);
        verify(experimentProcessManager).cancelExperiment(experiment);
    }

    private void saveExperiment(String requestId) {
        Experiment experiment = TestHelperUtils.createExperiment(requestId);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
    }
}
