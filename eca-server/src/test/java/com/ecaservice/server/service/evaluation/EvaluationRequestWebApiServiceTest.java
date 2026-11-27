package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.bpm.model.EvaluationRequestModel;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.dto.CreateEvaluationRequestDto;
import com.ecaservice.server.dto.CreateOptimalEvaluationRequestDto;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.UserService;
import com.ecaservice.web.dto.model.CreateEvaluationResponseDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EvaluationRequestWebApiService} class.
 *
 * @author Roman Batygin
 */
@Import({EvaluationRequestWebApiService.class, EvaluationLogMapperImpl.class,
        InstancesInfoMapperImpl.class, DateTimeConverter.class})
class EvaluationRequestWebApiServiceTest extends AbstractJpaTest {

    private static final String CURRENT_USER = "test-user";

    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @MockBean
    private CrossValidationConfig crossValidationConfig;

    @MockBean
    private UserService userService;

    @MockBean
    private EvaluationProcessManager evaluationProcessManager;

    @Autowired
    private EvaluationRequestWebApiService evaluationRequestWebApiService;

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void testCreateEvaluationRequestSuccess() {
        CreateEvaluationRequestDto requestDto = new CreateEvaluationRequestDto();
        requestDto.setInstancesUuid(UUID.randomUUID().toString());
        requestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        requestDto.setClassifierOptions(TestHelperUtils.createLogisticOptions());

        when(userService.getCurrentUser()).thenReturn(CURRENT_USER);

        doAnswer(invocation -> {
            EvaluationRequestModel model = invocation.getArgument(0);
            saveEvaluationLog(model.getRequestId());
            return null;
        }).when(evaluationProcessManager).createAndProcessEvaluationRequest(any(EvaluationRequestModel.class));

        CreateEvaluationResponseDto response = evaluationRequestWebApiService.createEvaluationRequest(requestDto);

        ArgumentCaptor<EvaluationRequestModel> modelCaptor = ArgumentCaptor.forClass(EvaluationRequestModel.class);
        verify(evaluationProcessManager).createAndProcessEvaluationRequest(modelCaptor.capture());

        EvaluationRequestModel capturedModel = modelCaptor.getValue();
        assertThat(capturedModel.getRequestId()).isNotBlank();
        assertThat(UUID.fromString(capturedModel.getRequestId())).isNotNull();
        assertThat(capturedModel.getCreatedBy()).isEqualTo(CURRENT_USER);
        assertThat(MDC.get(TX_ID)).isEqualTo(capturedModel.getRequestId());

        EvaluationLog savedLog = evaluationLogRepository.findByRequestId(capturedModel.getRequestId()).orElseThrow();
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedLog.getId());
        assertThat(response.getRequestId()).isEqualTo(capturedModel.getRequestId());
    }

    @Test
    void testCreateOptimalEvaluationRequestSuccess() {
        CreateOptimalEvaluationRequestDto requestDto = new CreateOptimalEvaluationRequestDto();
        requestDto.setInstancesUuid(UUID.randomUUID().toString());
        requestDto.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);

        when(userService.getCurrentUser()).thenReturn(CURRENT_USER);

        doAnswer(invocation -> {
            EvaluationRequestModel model = invocation.getArgument(0);
            saveEvaluationLog(model.getRequestId());
            return null;
        }).when(evaluationProcessManager).createAndProcessEvaluationRequest(any(EvaluationRequestModel.class));

        CreateEvaluationResponseDto response = evaluationRequestWebApiService.createOptimalEvaluationRequest(requestDto);

        ArgumentCaptor<EvaluationRequestModel> modelCaptor = ArgumentCaptor.forClass(EvaluationRequestModel.class);
        verify(evaluationProcessManager).createAndProcessEvaluationRequest(modelCaptor.capture());

        EvaluationRequestModel capturedModel = modelCaptor.getValue();
        assertThat(capturedModel.getRequestId()).isNotBlank();
        assertThat(capturedModel.getCreatedBy()).isEqualTo(CURRENT_USER);
        assertThat(MDC.get(TX_ID)).isEqualTo(capturedModel.getRequestId());

        EvaluationLog savedLog = evaluationLogRepository.findByRequestId(capturedModel.getRequestId()).orElseThrow();
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedLog.getId());
        assertThat(response.getRequestId()).isEqualTo(capturedModel.getRequestId());
    }

    private void saveEvaluationLog(String requestId) {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLog.setRequestId(requestId);
        instancesInfoRepository.save(evaluationLog.getInstancesInfo());
        evaluationLogRepository.save(evaluationLog);
    }
}
