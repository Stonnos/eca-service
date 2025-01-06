package com.ecaservice.core.transactional.outbox.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ecaservice.core.transactional.outbox.config.TransactionalOutboxAutoConfiguration.DEFAULT_EXCEPTION_STRATEGY;

/**
 * Annotation to support outbox sent mechanism for specified method.
 *
 * @author Roman Batygin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OutboxSender {

    /**
     * Message unique code
     *
     * @return request type
     */
    String value() default "";

    /**
     * The bean name of the custom {@link com.ecaservice.core.transactional.outbox.error.ExceptionStrategy}.
     *
     * @return exception strategy bean name
     */
    String exceptionStrategy() default DEFAULT_EXCEPTION_STRATEGY;
}
