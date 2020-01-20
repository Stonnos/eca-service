package com.ecaservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Response body matcher.
 *
 * @author Roman Batygin
 */
public class ResponseBodyMatcher {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Matches response body.
     *
     * @param expected    - expected object
     * @param <T>         - response generic type
     * @return result matcher
     */
    public <T> ResultMatcher containsBody(T expected) {
        return mvcResult -> {
            String actual = mvcResult.getResponse().getContentAsString();
            assertThat(objectMapper.writeValueAsString(expected)).isEqualToIgnoringWhitespace(actual);
        };
    }

    /**
     * Creates response body matcher.
     *
     * @return response body matcher
     */
    public static ResponseBodyMatcher responseBody() {
        return new ResponseBodyMatcher();
    }
}