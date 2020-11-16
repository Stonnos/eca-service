package com.ecaservice.external.api.aspect;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.error.ExceptionTranslator;
import com.ecaservice.external.api.exception.DataNotFoundException;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.external.api.service.RequestStageHandler;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link ErrorHandlerAspect} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ErrorHandlerAspectTest {

    @Mock
    private MessageCorrelationService messageCorrelationService;
    @Mock
    private ExceptionTranslator exceptionTranslator;
    @Mock
    private RequestStageHandler requestStageHandler;

    @InjectMocks
    private ErrorHandlerAspect errorHandlerAspect;

    @Captor
    private ArgumentCaptor<EvaluationResponseDto> evaluationResponseDtoArgumentCaptor;

    private EvaluationRequestEntity evaluationRequestEntity;
    private ProceedingJoinPoint joinPoint;
    private MonoSink<EvaluationResponseDto> sink;

    @BeforeEach
    void init() throws Throwable {
        evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        joinPoint = createProceedingJoinPoint(evaluationRequestEntity);
        when(exceptionTranslator.translate(any(Exception.class))).thenReturn(RequestStatus.ERROR);
        sink = mock(MonoSink.class);
        when(messageCorrelationService.pop(evaluationRequestEntity.getCorrelationId())).thenReturn(Optional.of(sink));
    }

    @Test
    void testMethodExecutionWithError() throws Throwable {
        errorHandlerAspect.around(joinPoint, null);
        verify(requestStageHandler).handleError(anyString(), any(Exception.class));
        verify(sink).success(evaluationResponseDtoArgumentCaptor.capture());
        EvaluationResponseDto evaluationResponseDto = evaluationResponseDtoArgumentCaptor.getValue();
        assertThat(evaluationResponseDto).isNotNull();
        assertThat(evaluationResponseDto.getRequestId()).isEqualTo(evaluationRequestEntity.getCorrelationId());
        assertThat(evaluationResponseDto.getStatus()).isEqualTo(RequestStatus.ERROR);
    }

    private ProceedingJoinPoint createProceedingJoinPoint(EvaluationRequestEntity evaluationRequestEntity)
            throws Throwable {
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {
                evaluationRequestEntity
        });
        doThrow(DataNotFoundException.class).when(proceedingJoinPoint).proceed();
        return proceedingJoinPoint;
    }
}
