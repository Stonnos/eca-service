package com.ecaservice.core.lock.aspect;

import com.ecaservice.common.web.expression.SpelExpressionHelper;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.core.lock.exception.CannotUnlockException;
import com.ecaservice.core.lock.fallback.FallbackHandler;
import com.ecaservice.core.lock.metrics.LockMeterService;
import com.ecaservice.core.lock.service.LockRegistryRepository;
import com.ecaservice.core.lock.service.LockService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Aspect for lock execution.
 *
 * @author Roman Batygin
 */
@Slf4j
@Aspect
@Order(LockExecutionAspect.LOCK_ADVICE_ORDER)
@Component
public class LockExecutionAspect {

    /**
     * Lock advice order
     */
    public static final int LOCK_ADVICE_ORDER = 100;

    private static final String LOCK_KEY_FORMAT = "%s-%s";

    private final SpelExpressionHelper spelExpressionHelper = new SpelExpressionHelper();

    private final ApplicationContext applicationContext;
    private final LockRegistryRepository lockRegistryRepository;
    private final LockMeterService lockMeterService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param applicationContext     - spring application context bean
     * @param lockRegistryRepository - lock registry repository
     * @param lockMeterService       - lock meter service bean
     */
    public LockExecutionAspect(ApplicationContext applicationContext,
                               LockRegistryRepository lockRegistryRepository,
                               LockMeterService lockMeterService) {
        this.applicationContext = applicationContext;
        this.lockRegistryRepository = lockRegistryRepository;
        this.lockMeterService = lockMeterService;
    }

    /**
     * Wrapper for service method to perform lock.
     *
     * @param joinPoint - give reflective access to the processed method
     * @param locked    - locked annotation
     * @return result object
     */
    @Around("execution(@com.ecaservice.core.lock.annotation.Locked * * (..)) && @annotation(locked)")
    public Object around(ProceedingJoinPoint joinPoint, Locked locked) throws Throwable {
        log.debug("Starting to around locked method [{}]", joinPoint.getSignature().getName());
        String lockKey = getLockKey(joinPoint, locked.lockName(), locked.key());
        LockRegistry lockRegistry = lockRegistryRepository.getRegistry(locked.lockRegistryKey());
        LockService lockService = new LockService(lockRegistry);
        try {
            if (!locked.waitForLock()) {
                if (!lockService.tryLock(lockKey)) {
                    lockMeterService.trackFailedLock(locked.lockName());
                    var fallbackHandler = applicationContext.getBean(locked.fallback(), FallbackHandler.class);
                    fallbackHandler.fallback(lockKey);
                    return null;
                }
            } else {
                lockService.lock(lockKey);
            }
            lockMeterService.trackSuccessLock(locked.lockName());
            Object result = joinPoint.proceed();
            unlock(lockService, locked.lockName(), lockKey);
            log.debug("Around locked method [{}] has been processed", joinPoint.getSignature().getName());
            return result;
        } catch (CannotAcquireLockException ex) {
            log.error("Acquire lock error: {}", ex.getMessage());
            lockMeterService.trackAcquireLockError(locked.lockName());
            throw ex;
        } catch (Exception ex) {
            log.error("There was an error while around method [{}] with Locked: {}",
                    joinPoint.getSignature().getName(), ex.getMessage());
            unlock(lockService, locked.lockName(), lockKey);
            throw ex;
        }
    }

    private void unlock(LockService lockService, String lockName, String key) {
        try {
            lockService.unlock(key);
            lockMeterService.trackSuccessUnlock(lockName);
        } catch (CannotUnlockException e) {
            log.error("There was an error while release lock with key [{}]: {}", key, e.getMessage());
            lockMeterService.trackUnlockError(lockName);
        }
    }

    private String getLockKey(ProceedingJoinPoint joinPoint, String lockName, String lockKey) {
        if (!StringUtils.hasText(lockKey)) {
            return lockName;
        } else {
            Object value = spelExpressionHelper.parseExpression(joinPoint, lockKey);
            return String.format(LOCK_KEY_FORMAT, lockName, value);
        }
    }
}
