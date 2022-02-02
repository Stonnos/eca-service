package com.ecaservice.core.audit.aspect;

import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.core.audit.annotation.Audits;
import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.service.AuditEventInitiator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuditAspect} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class AuditAspectTest {

    private static final String AUDIT_CODE = "AUDIT_CODE";
    private static final String OUTPUT_VALUE = "outputValue";
    private static final String INPUT_VALUE_1 = "inputValue1";
    private static final String INPUT_VALUE_2 = "inputValue2";
    private static final String USER = "user";
    private static final String METHOD_NAME = "methodName";
    private static final String PARAM_1 = "param1";
    private static final String PARAM_2 = "param2";

    private static final String[] PARAMETERS_NAMES = {PARAM_1, PARAM_2};
    private static final String RESULT_EXPRESSION = "result";

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private AuditEventInitiator auditEventInitiator;

    @InjectMocks
    private AuditAspect auditAspect;

    @Captor
    private ArgumentCaptor<AuditEvent> auditEventArgumentCaptor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestObject {

        String x;
    }

    @Test
    void testAroundAuditMethodWithEventInitiator() throws Throwable {
        var audit = mock(Audit.class);
        when(audit.value()).thenReturn(AUDIT_CODE);
        var joinPoint = mockProceedingJoinPoint(OUTPUT_VALUE);
        when(auditEventInitiator.getInitiator()).thenReturn(USER);
        auditAspect.around(joinPoint, audit);
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(auditEventArgumentCaptor.capture());
        var auditEvent = auditEventArgumentCaptor.getValue();
        assertThat(auditEvent).isNotNull();
        assertThat(auditEvent.getAuditCode()).isEqualTo(AUDIT_CODE);
        assertThat(auditEvent.getEventType()).isEqualTo(EventType.SUCCESS);
        assertThat(auditEvent.getInitiator()).isEqualTo(USER);
        assertThat(auditEvent.getAuditContextParams()).isNotNull();
        assertThat(auditEvent.getAuditContextParams().getInputParams())
                .containsEntry(PARAM_1, INPUT_VALUE_1)
                .containsEntry(PARAM_2, INPUT_VALUE_2);
        assertThat(auditEvent.getAuditContextParams().getReturnValue()).isEqualTo(OUTPUT_VALUE);
    }

    @Test
    void testAroundAuditsMethod() throws Throwable {
        var audits = mock(Audits.class);
        var audit = mock(Audit.class);
        when(audit.value()).thenReturn(AUDIT_CODE);
        var auditsValue = new Audit[] {audit, audit};
        when(audits.value()).thenReturn(auditsValue);
        var joinPoint = mockProceedingJoinPoint(OUTPUT_VALUE);
        when(auditEventInitiator.getInitiator()).thenReturn(USER);
        auditAspect.around(joinPoint, audits);
        verify(applicationEventPublisher, times(auditsValue.length)).publishEvent(any(AuditEvent.class));
    }

    @Test
    void testAroundAuditMethodWithSourceInitiator() throws Throwable {
        var audit = mock(Audit.class);
        when(audit.value()).thenReturn(AUDIT_CODE);
        when(audit.sourceInitiator()).thenReturn(String.format("#%s", PARAM_1));
        var joinPoint = mockProceedingJoinPoint(OUTPUT_VALUE);
        auditAspect.around(joinPoint, audit);
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(auditEventArgumentCaptor.capture());
        var auditEvent = auditEventArgumentCaptor.getValue();
        assertThat(auditEvent).isNotNull();
        assertThat(auditEvent.getInitiator()).isEqualTo(INPUT_VALUE_1);
    }

    @Test
    void testAroundAuditMethodWithTargetInitiator() throws Throwable {
        var audit = mock(Audit.class);
        when(audit.value()).thenReturn(AUDIT_CODE);
        when(audit.targetInitiator()).thenReturn(RESULT_EXPRESSION);
        var joinPoint = mockProceedingJoinPoint(OUTPUT_VALUE);
        auditAspect.around(joinPoint, audit);
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(auditEventArgumentCaptor.capture());
        var auditEvent = auditEventArgumentCaptor.getValue();
        assertThat(auditEvent).isNotNull();
        assertThat(auditEvent.getInitiator()).isEqualTo(OUTPUT_VALUE);
    }

    @Test
    void testAroundAuditMethodWithSourceCorrelationId() throws Throwable {
        var audit = mock(Audit.class);
        when(audit.value()).thenReturn(AUDIT_CODE);
        when(audit.sourceCorrelationIdKey()).thenReturn(String.format("#%s", PARAM_1));
        var joinPoint = mockProceedingJoinPoint(OUTPUT_VALUE);
        auditAspect.around(joinPoint, audit);
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(auditEventArgumentCaptor.capture());
        var auditEvent = auditEventArgumentCaptor.getValue();
        assertThat(auditEvent).isNotNull();
        assertThat(auditEvent.getCorrelationId()).isEqualTo(INPUT_VALUE_1);
    }

    @Test
    void testAroundAuditMethodWithTargetCorrelationId() throws Throwable {
        var audit = mock(Audit.class);
        when(audit.value()).thenReturn(AUDIT_CODE);
        when(audit.targetCorrelationIdKey()).thenReturn(RESULT_EXPRESSION);
        var joinPoint = mockProceedingJoinPoint(OUTPUT_VALUE);
        auditAspect.around(joinPoint, audit);
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(auditEventArgumentCaptor.capture());
        var auditEvent = auditEventArgumentCaptor.getValue();
        assertThat(auditEvent).isNotNull();
        assertThat(auditEvent.getCorrelationId()).isEqualTo(OUTPUT_VALUE);
    }

    @Test
    void testAroundAuditMethodWithTargetCorrelationIdAsObjectFieldRef() throws Throwable {
        var audit = mock(Audit.class);
        when(audit.value()).thenReturn(AUDIT_CODE);
        when(audit.targetCorrelationIdKey()).thenReturn("x");
        var object = new TestObject("xValue");
        var joinPoint = mockProceedingJoinPoint(object);
        auditAspect.around(joinPoint, audit);
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(auditEventArgumentCaptor.capture());
        var auditEvent = auditEventArgumentCaptor.getValue();
        assertThat(auditEvent).isNotNull();
        assertThat(auditEvent.getCorrelationId()).isEqualTo(object.getX());
    }

    private ProceedingJoinPoint mockProceedingJoinPoint(Object returnValue) throws Throwable {
        ProceedingJoinPoint proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {INPUT_VALUE_1, INPUT_VALUE_2});
        when(proceedingJoinPoint.proceed()).thenReturn(returnValue);
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getName()).thenReturn(METHOD_NAME);
        when(signature.getParameterNames()).thenReturn(PARAMETERS_NAMES);
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        return proceedingJoinPoint;
    }
}
