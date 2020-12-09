package com.ecaservice.oauth2.test.controller;

import com.ecaservice.oauth2.test.configuration.annotation.Oauth2TestConfiguration;
import com.ecaservice.oauth2.test.token.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

/**
 * Abstract class for controllers tests.
 *
 * @author Roman Batygin
 */
@Oauth2TestConfiguration
public abstract class AbstractControllerTest {

    private static final String BEARER_TOKEN_FORMAT = "Bearer %s";

    @Getter
    private String accessToken;

    @Inject
    private TokenService tokenService;

    @Inject
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() throws Exception {
        accessToken = tokenService.obtainAccessToken();
        before();
    }

    public String getBearerToken() {
        return String.format(BEARER_TOKEN_FORMAT, getAccessToken());
    }

    public void before() {
    }
}
