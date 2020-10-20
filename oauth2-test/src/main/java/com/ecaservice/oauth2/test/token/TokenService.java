package com.ecaservice.oauth2.test.token;

import com.ecaservice.oauth2.test.configuration.Oauth2TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private static final String CREDENTIALS_FORMAT = "%s:%s";
    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";
    private static final String TOKEN_URL = "/oauth/token";

    private static final String BASIC_FORMAT = "Basic %s";

    private final Oauth2TestConfig oauth2TestConfig;
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
        params.addAll(GRANT_TYPE_PARAM, Arrays.asList(oauth2TestConfig.getGrantTypes()));
        params.add(USERNAME_PARAM, oauth2TestConfig.getUsername());
        params.add(PASSWORD_PARAM, oauth2TestConfig.getPassword());
        String credentials =
                String.format(CREDENTIALS_FORMAT, oauth2TestConfig.getClientId(), oauth2TestConfig.getSecret());
        String base64Credentials = Base64Utils.encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        ResultActions result = mockMvc.perform(post(TOKEN_URL)
                .params(params)
                .header(HttpHeaders.AUTHORIZATION, String.format(BASIC_FORMAT, base64Credentials)))
                .andExpect(status().isOk());
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, TokenResponse.class).getAccessToken();
    }
}