package com.ecaservice.core.redelivery.error;

import feign.FeignException;
import feign.Request;
import feign.RetryableException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link FeignExceptionStrategy} class.
 *
 * @author Roman Batygin
 */
class FeignExceptionStrategyTest {

    private static final String MESSAGE = "message";
    private static final int STATUS = 503;

    private final FeignExceptionStrategy feignExceptionStrategy = new FeignExceptionStrategy();

    private Request request;

    @BeforeEach
    void init() {
        request = Request.create(Request.HttpMethod.GET, StringUtils.EMPTY,
                Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
    }

    @Test
    void testServiceUnavailableException() {
        boolean result = feignExceptionStrategy.notFatal(
                new FeignException.ServiceUnavailable(MESSAGE, request, null));
        assertThat(result).isTrue();
    }

    @Test
    void testRetryableException() {
        boolean result = feignExceptionStrategy.notFatal(
                new RetryableException(STATUS, MESSAGE, Request.HttpMethod.GET, null, request));
        assertThat(result).isTrue();
    }

    @Test
    void testRuntimeException() {
        boolean result = feignExceptionStrategy.notFatal(
                new FeignException.BadRequest(MESSAGE, request, null));
        assertThat(result).isFalse();
    }
}
