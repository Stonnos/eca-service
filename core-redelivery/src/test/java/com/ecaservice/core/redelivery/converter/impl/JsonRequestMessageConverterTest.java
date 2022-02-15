package com.ecaservice.core.redelivery.converter.impl;

import com.ecaservice.core.redelivery.model.TestRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link JsonRequestMessageConverter} class.
 *
 * @author Roman Batygin
 */
class JsonRequestMessageConverterTest {

    private JsonRequestMessageConverter jsonRequestMessageConverter;

    @BeforeEach
    void init() {
        jsonRequestMessageConverter = new JsonRequestMessageConverter(new ObjectMapper());
    }

    @Test
    void testConversion() throws JsonProcessingException {
        var testRequest = new TestRequest(0, 1);
        var json = jsonRequestMessageConverter.convert(testRequest);
        assertThat(json).isNotNull();
        var actualRequest = jsonRequestMessageConverter.convert(json, TestRequest.class);
        assertThat(actualRequest).isEqualTo(testRequest);
    }
}
