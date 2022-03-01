package com.ecaservice.core.lock.annotation;

import com.ecaservice.core.lock.aspect.LockExecutionAspect;
import com.ecaservice.core.lock.config.CoreLockConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations that enables locks management.
 *
 * @author Roman Batygin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({LockExecutionAspect.class, CoreLockConfiguration.class})
public @interface EnableLocks {
}
