package com.ecaservice.core.lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method must be try locked. In this implementation, if the thread cannot acquire the lock,
 * then it does not enter the wait state and further control is immediately transferred to the calling code.
 * Note that this annotation can be used only for void methods.
 *
 * @author Roman Batygin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TryLocked {

    /**
     * Gets lock name.
     *
     * @return lock name
     */
    String lockName() default "";

    /**
     * Spring Expression Language (SpEL) attribute for computing the key dynamically.
     */
    String key() default "";

    /**
     * Gets lock registry bean name. Bean must implements
     * {@link org.springframework.integration.support.locks.LockRegistry} interface.
     *
     * @return lock registry bean name
     */
    String lockRegistry() default "redisLockRegistry";
}
