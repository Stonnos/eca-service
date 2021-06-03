package com.ecaservice.core.lock.redis.annotation;

import com.ecaservice.core.lock.redis.config.RedisLockConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations that enables redis locks management.
 *
 * @author Roman Batygin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RedisLockConfiguration.class)
public @interface EnableRedisLocks {
}
