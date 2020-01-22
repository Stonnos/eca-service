package com.ecaservice.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Service for fetching oauth2 access token.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String USERNAME_PARAM = USERNAME;
    private static final String PASSWORD_PARAM = "password";
    private static final String TOKEN_URL = "/oauth/token";

    private static final String BASIC_FORMAT = "Basic %s";

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Gets access token from oauth2 server.
     *
     * @return access token
     * @throws Exception in case of error
     */
    public String obtainAccessToken() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(GRANT_TYPE_PARAM, GRANT_TYPE_PASSWORD);
        params.add(USERNAME_PARAM, "admin");
        params.add(PASSWORD_PARAM, "secret");
        String credentials = Base64Utils.encodeToString("client:secret".getBytes(StandardCharsets.UTF_8));
        ResultActions result = mockMvc.perform(post(TOKEN_URL)
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, String.format(BASIC_FORMAT, credentials))
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, TokenResponse.class).getAccessToken();
    }
}
