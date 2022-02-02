package com.ecaservice.core.audit.aspect;

import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.core.audit.annotation.Audits;
import com.ecaservice.core.audit.event.AuditEvent;
import com.ecaservice.core.audit.model.AuditContextParams;
import com.ecaservice.core.audit.service.AuditEventInitiator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.stream.IntStream;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Aspect for audit execution.
 *
 * @author Roman Batygin
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private static final String RESULT_EXPRESSION = "result";

    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuditEventInitiator auditEventInitiator;
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * Wrapper to audit service method.
     *
     * @param joinPoint - give reflective access to the processed method
     * @param audit     - audit annotation
     * @return result object
     */
    @Around("execution(@com.ecaservice.core.audit.annotation.Audit * * (..)) && @annotation(audit)")
    public Object around(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {
        log.debug("Starting to around audited method [{}]", joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        publishAuditEvent(audit, joinPoint, result);
        log.debug("Around audited method [{}] has been processed", joinPoint.getSignature().getName());
        return result;
    }

    /**
     * Wrapper to audit service method.
     *
     * @param joinPoint - give reflective access to the processed method
     * @param audits    - audits annotation
     * @return result object
     */
    @Around("execution(@com.ecaservice.core.audit.annotation.Audits * * (..)) && @annotation(audits)")
    public Object around(ProceedingJoinPoint joinPoint, Audits audits) throws Throwable {
        log.debug("Starting to around audited method [{}]", joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        for (Audit audit : audits.value()) {
            publishAuditEvent(audit, joinPoint, result);
        }
        log.debug("Around audited method [{}] has been processed", joinPoint.getSignature().getName());
        return result;
    }

    private void publishAuditEvent(Audit audit, ProceedingJoinPoint joinPoint, Object result) {
        Map<String, Object> methodParams = getMethodParams(joinPoint);
        AuditContextParams auditContextParams = new AuditContextParams(methodParams, result);
        String eventInitiator = getInitiator(audit, joinPoint, result);
        String correlationId = getCorrelationId(audit, joinPoint, result);
        AuditEvent auditEvent = new AuditEvent(this, audit.value(), EventType.SUCCESS, correlationId,
                eventInitiator, auditContextParams);
        applicationEventPublisher.publishEvent(auditEvent);
    }

    private String getInitiator(Audit audit, ProceedingJoinPoint joinPoint, Object methodResult) {
        if (StringUtils.hasText(audit.sourceInitiator())) {
            return parseMethodParameterExpression(audit.sourceInitiator(), joinPoint);
        } else if (StringUtils.hasText(audit.targetInitiator())) {
            return parseMethodResultExpression(audit.targetInitiator(), methodResult);
        } else {
            return auditEventInitiator.getInitiator();
        }
    }

    private String getCorrelationId(Audit audit, ProceedingJoinPoint joinPoint, Object methodResult) {
        if (StringUtils.hasText(audit.sourceCorrelationIdKey())) {
            return parseMethodParameterExpression(audit.sourceCorrelationIdKey(), joinPoint);
        } else if (StringUtils.hasText(audit.targetCorrelationIdKey())) {
            return parseMethodResultExpression(audit.targetCorrelationIdKey(), methodResult);
        } else {
            return null;
        }
    }

    private String parseMethodParameterExpression(String expression, ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] methodParameters = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        IntStream.range(0, methodParameters.length).forEach(i -> context.setVariable(methodParameters[i], args[i]));
        Object value = expressionParser.parseExpression(expression).getValue(context, Object.class);
        return String.valueOf(value);
    }

    private String parseMethodResultExpression(String expression, Object methodResult) {
        if (RESULT_EXPRESSION.equals(expression)) {
            return String.valueOf(methodResult);
        } else {
            StandardEvaluationContext context = new StandardEvaluationContext(methodResult);
            Object val = expressionParser.parseExpression(expression).getValue(context, Object.class);
            return String.valueOf(val);
        }
    }

    private Map<String, Object> getMethodParams(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Map<String, Object> paramsMap = newHashMap();
        String[] parameterNames = methodSignature.getParameterNames();
        if (parameterNames == null || parameterNames.length == 0) {
            return Collections.emptyMap();
        }
        Object[] args = joinPoint.getArgs();
        IntStream.range(0, args.length).forEach(i -> paramsMap.put(parameterNames[i], args[i]));
        return paramsMap;
    }
}
