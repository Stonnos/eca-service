package com.ecaservice.external.api.error;

import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.external.api.exception.DataNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ExceptionTranslator} class functionality.
 *
 * @author Roman Batygin
 */
class ExceptionTranslatorTest {

    private ExceptionTranslator exceptionTranslator = new ExceptionTranslator();

    @Test
    void testDataNotFound() {
        internalTestException(new DataNotFoundException(StringUtils.EMPTY), ResponseCode.DATA_NOT_FOUND);
    }

    @Test
    void testServiceUnavailable() {
        internalTestException(new AmqpException(StringUtils.EMPTY), ResponseCode.SERVICE_UNAVAILABLE);
    }

    @Test
    void testError() {
        internalTestException(new IllegalStateException(StringUtils.EMPTY), ResponseCode.ERROR);
    }

    private void internalTestException(Exception ex, ResponseCode expected) {
        ResponseCode actual = exceptionTranslator.translate(ex);
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }
}
