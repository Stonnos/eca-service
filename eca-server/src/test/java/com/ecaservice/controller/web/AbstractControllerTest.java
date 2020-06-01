package com.ecaservice.controller.web;

import com.ecaservice.configuation.annotation.Oauth2TestConfiguration;
import com.ecaservice.token.TokenService;
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
abstract class AbstractControllerTest {

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
    }
}
