package com.ecaservice.oauth2.test.controller;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.oauth2.test.configuration.annotation.Oauth2ResourceServerTestConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static com.ecaservice.oauth2.test.util.TokenUtils.ACCESS_TOKEN;

/**
 * Abstract class for controllers tests.
 *
 * @author Roman Batygin
 */
@EnableGlobalExceptionHandler
@Oauth2ResourceServerTestConfiguration
public abstract class AbstractControllerTest {

    private static final String BEARER_TOKEN_FORMAT = "Bearer %s";

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        before();
    }

    public String getBearerToken() {
        return String.format(BEARER_TOKEN_FORMAT, ACCESS_TOKEN);
    }

    public void before() {
    }
}
