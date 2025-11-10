package com.ecaservice.oauth.controller.internal;

import com.ecaservice.oauth.service.PersonalAccessTokenService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.user.dto.PersonalAccessTokenInfoDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link PersonalAccessTokenApiController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = PersonalAccessTokenApiController.class)
class PersonalAccessTokenApiControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/api/internal/personal-access-token";
    private static final String VERIFY_TOKEN_URL = BASE_URL + "/verify-token";
    private static final String TOKEN_PARAM = "token";

    @MockBean
    private PersonalAccessTokenService personalAccessTokenService;

    @Test
    void testVerifyTokenUnauthorized() throws Exception {
        mockMvc.perform(get(VERIFY_TOKEN_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testVerifyTokenOk() throws Exception {
        var tokenInfoDto = new PersonalAccessTokenInfoDto();
        tokenInfoDto.setValid(true);
        String token = UUID.randomUUID().toString();
        when(personalAccessTokenService.verifyToken(anyString())).thenReturn(tokenInfoDto);
        mockMvc.perform(get(VERIFY_TOKEN_URL)
                        .param(TOKEN_PARAM, token)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(tokenInfoDto)));
    }

    @Test
    void testVerifyEmptyTokenBadRequest() throws Exception {
        mockMvc.perform(get(VERIFY_TOKEN_URL)
                        .param(TOKEN_PARAM, StringUtils.EMPTY)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }
}
