package com.ecaservice.core.audit.aspect;

import com.ecaservice.core.audit.annotation.Auditable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

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

    /**
     * Wrapper to audit service method.
     *
     * @param joinPoint - give reflective access to the processed method
     * @param auditable - audit annotation
     * @return result object
     */
    @Around("execution(@com.ecaservice.core.audit.annotation.Auditable * * (..)) && @annotation(auditable)")
    public Object around(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
       /* String lockKey = getLockKey(joinPoint, locked);
        LockRegistry lockRegistry = applicationContext.getBean(locked.lockRegistry(), LockRegistry.class);
        LockService lockService = new LockService(lockRegistry);
        try {
            lockService.lock(lockKey);
            Object result = joinPoint.proceed();
            lockService.unlock(lockKey);
            return result;
        } catch (CannotAcquireLockException ex) {
            log.error("Acquire lock error: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            lockService.unlock(lockKey);
            throw ex;
        }*/
        return null;
    }
}
