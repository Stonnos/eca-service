package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.service.ChangePasswordService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import static com.ecaservice.oauth.TestHelperUtils.createChangePasswordRequestEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ChangePasswordController} functionality.
 *
 * @author Roman Batygin
 */
@EnableGlobalExceptionHandler
@WebMvcTest(controllers = ChangePasswordController.class)
class ChangePasswordControllerTest extends AbstractControllerTest {

    private static final String TOKEN_PARAM = "token";
    private static final String TOKEN_VALUE = "tokenValue";

    private static final String BASE_URL = "/password/change";
    private static final String CONFIRM_URL = BASE_URL + "/confirm";
    private static final String REQUEST_URL = BASE_URL + "/request";
    private static final String PASSWORD = "pa66word!";

    @MockBean
    private ChangePasswordService changePasswordService;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @Inject
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateChangePasswordRequestWithEmptyOldPassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(StringUtils.EMPTY, PASSWORD);
        mockMvc.perform(post(REQUEST_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateChangePasswordRequestWithEmptyNewPassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, StringUtils.EMPTY);
        mockMvc.perform(post(REQUEST_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateChangePasswordUnauthorized() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, PASSWORD);
        mockMvc.perform(post(REQUEST_URL)
                .content(objectMapper.writeValueAsString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateChangePasswordRequestOk() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, PASSWORD);
        when(changePasswordService.createChangePasswordRequest(anyLong(), any(ChangePasswordRequest.class)))
                .thenReturn(TokenModel.builder().build());
        mockMvc.perform(post(REQUEST_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testConfirmChangePasswordRequestUnauthorized() throws Exception {
        mockMvc.perform(post(CONFIRM_URL)
                .param(TOKEN_PARAM, TOKEN_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testConfirmChangePasswordRequest() throws Exception {
        var changePasswordRequestEntity = createChangePasswordRequestEntity(TOKEN_VALUE);
        when(changePasswordService.changePassword(TOKEN_VALUE)).thenReturn(changePasswordRequestEntity);
        mockMvc.perform(post(CONFIRM_URL)
                .param(TOKEN_PARAM, TOKEN_VALUE)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk());
        verify(changePasswordService, atLeastOnce()).changePassword(TOKEN_VALUE);
    }

    @Test
    void testConfirmChangePasswordRequestWithNullToken() throws Exception {
        mockMvc.perform(post(CONFIRM_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }
}
