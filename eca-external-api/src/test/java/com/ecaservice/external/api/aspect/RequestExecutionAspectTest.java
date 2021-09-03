package com.ecaservice.external.api.aspect;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.error.ExceptionTranslator;
import com.ecaservice.external.api.exception.DataNotFoundException;
import com.ecaservice.external.api.metrics.MetricsService;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.external.api.service.RequestStageHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.MonoSink;

import java.util.Optional;
import java.util.UUID;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link RequestExecutionAspect} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class RequestExecutionAspectTest {

    @Mock
    private MessageCorrelationService messageCorrelationService;
    @Mock
    private MetricsService metricsService;
    @Mock
    private ExceptionTranslator exceptionTranslator;
    @Mock
    private RequestStageHandler requestStageHandler;

    @InjectMocks
    private RequestExecutionAspect requestExecutionAspect;

    @Captor
    private ArgumentCaptor<ResponseDto<EvaluationResponseDto>> evaluationResponseDtoArgumentCaptor;

    @Test
    void testSuccessMethodExecution() throws Throwable {
        EvaluationRequestEntity evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        ProceedingJoinPoint joinPoint = createProceedingJoinPoint(evaluationRequestEntity);
        MonoSink<ResponseDto<EvaluationResponseDto>> sink = mock(MonoSink.class);
        when(messageCorrelationService.pop(evaluationRequestEntity.getCorrelationId())).thenReturn(Optional.of(sink));
        requestExecutionAspect.around(joinPoint, null);
        verify(sink).success(evaluationResponseDtoArgumentCaptor.capture());
        ResponseDto<EvaluationResponseDto> evaluationResponseDto = evaluationResponseDtoArgumentCaptor.getValue();
        assertThat(evaluationResponseDto).isNotNull();
        assertThat(evaluationResponseDto.getPayload()).isNotNull();
        assertThat(evaluationResponseDto.getPayload().getRequestId()).isEqualTo(
                evaluationRequestEntity.getCorrelationId());
        assertThat(evaluationResponseDto.getPayload().getEvaluationStatus()).isEqualTo(EvaluationStatus.IN_PROGRESS);
        assertThat(evaluationResponseDto.getResponseCode()).isEqualTo(ResponseCode.SUCCESS);
    }

    @Test
    void testMethodExecutionWithError() throws Throwable {
        EvaluationRequestEntity evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        ProceedingJoinPoint joinPoint = createProceedingJoinPoint(evaluationRequestEntity);
        doThrow(new DataNotFoundException("error")).when(joinPoint).proceed();
        when(exceptionTranslator.translate(any(Exception.class))).thenReturn(ResponseCode.ERROR);
        MonoSink<ResponseDto<EvaluationResponseDto>> sink = mock(MonoSink.class);
        when(messageCorrelationService.pop(evaluationRequestEntity.getCorrelationId())).thenReturn(Optional.of(sink));
        requestExecutionAspect.around(joinPoint, null);
        verify(requestStageHandler).handleError(any(EcaRequestEntity.class), any(String.class));
        verify(sink).success(evaluationResponseDtoArgumentCaptor.capture());
        ResponseDto<EvaluationResponseDto> evaluationResponseDto = evaluationResponseDtoArgumentCaptor.getValue();
        assertThat(evaluationResponseDto).isNotNull();
        assertThat(evaluationResponseDto.getPayload()).isNotNull();
        assertThat(evaluationResponseDto.getPayload().getRequestId()).isEqualTo(
                evaluationRequestEntity.getCorrelationId());
        assertThat(evaluationResponseDto.getPayload().getEvaluationStatus()).isEqualTo(EvaluationStatus.ERROR);
        assertThat(evaluationResponseDto.getResponseCode()).isEqualTo(ResponseCode.ERROR);
    }

    @Test
    void testMethodExecutionWithNullArgs() {
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        when(proceedingJoinPoint.getArgs()).thenReturn(null);
        assertThrows(IllegalStateException.class, () -> requestExecutionAspect.around(proceedingJoinPoint, null));
    }

    @Test
    void testMethodExecutionWithNullArg() {
        ProceedingJoinPoint proceedingJoinPoint = createProceedingJoinPoint(null);
        assertThrows(IllegalStateException.class, () -> requestExecutionAspect.around(proceedingJoinPoint, null));
    }

    @Test
    void testMethodExecutionWithInvalidArgType() {
        ProceedingJoinPoint proceedingJoinPoint = createProceedingJoinPoint(new Object());
        assertThrows(IllegalStateException.class, () -> requestExecutionAspect.around(proceedingJoinPoint, null));
    }

    private ProceedingJoinPoint createProceedingJoinPoint(Object arg) {
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {arg});
        return proceedingJoinPoint;
    }
}
