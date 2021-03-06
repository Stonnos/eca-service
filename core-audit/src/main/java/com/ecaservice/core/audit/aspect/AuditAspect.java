package com.ecaservice.core.audit.aspect;

import com.ecaservice.audit.dto.EventType;
import com.ecaservice.core.audit.annotation.Audit;
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
        Map<String, Object> methodParams = getMethodParams(joinPoint);
        Object result = joinPoint.proceed();
        AuditContextParams auditContextParams = new AuditContextParams(methodParams, result);
        String eventInitiator = getInitiator(audit, result);
        AuditEvent auditEvent =
                new AuditEvent(this, audit.value(), EventType.SUCCESS, eventInitiator, auditContextParams);
        applicationEventPublisher.publishEvent(auditEvent);
        log.debug("Around audited method [{}] has been processed", joinPoint.getSignature().getName());
        return result;
    }

    private String getInitiator(Audit audit, Object methodResult) {
        if (!StringUtils.hasText(audit.targetInitiator())) {
            return auditEventInitiator.getInitiator();
        } else {
            StandardEvaluationContext context = new StandardEvaluationContext(methodResult);
            Object val = expressionParser.parseExpression(audit.targetInitiator()).getValue(context, Object.class);
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
