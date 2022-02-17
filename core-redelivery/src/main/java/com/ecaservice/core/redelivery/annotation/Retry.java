package com.ecaservice.core.redelivery.annotation;

import com.ecaservice.core.redelivery.callback.RetryCallback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to support redelivery mechanism for specified method.
 *
 * @author Roman Batygin
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {

    /**
     * Request type unique code
     *
     * @return request type
     */
    String value() default "";

    /**
     * Spring Expression Language (SpEL) attribute for computing the request id key dynamically. Expression may contain
     * method input parameter shortcut.
     * Method input parameters examples: {@code #id}, {@code #object.name} - for field in class object.
     */
    String requestIdKey() default "";

    /**
     * The bean name of the custom {@link com.ecaservice.core.redelivery.converter.RequestMessageConverter}.
     *
     * @return message converter bean name
     */
    String messageConverter() default "jsonRequestMessageConverter";

    /**
     * The bean name of the custom {@link com.ecaservice.core.redelivery.error.ExceptionStrategy}.
     *
     * @return exception strategy bean name
     */
    String exceptionStrategy() default "defaultExceptionStrategy";

    /**
     * Maximum retries. Value <= 0 means unlimited retries.
     *
     * @return maximum retries
     */
    int maxRetries() default -1;

    /**
     * The bean name of the custom {@link RetryCallback}.
     *
     * @return retry callback bean name
     */
    String retryCallback() default "defaultRetryCallback";
}
