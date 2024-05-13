package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.service.ChangePasswordService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.ChangePasswordRequestStatusDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.ecaservice.oauth.TestHelperUtils.createChangePasswordRequestEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private static final String CONFIRMATION_CODE_PARAM = "confirmationCode";
    private static final String CONFIRMATION_CODE = "code";

    private static final String TOKEN_VALUE = "token";

    private static final String BASE_URL = "/password/change";
    private static final String REQUEST_STATUS_URL = BASE_URL + "/request-status";
    private static final String CONFIRM_URL = BASE_URL + "/confirm";
    private static final String REQUEST_URL = BASE_URL + "/request";
    private static final String PASSWORD = "pa66word!";

    @MockBean
    private ChangePasswordService changePasswordService;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
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
        String token = UUID.randomUUID().toString();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, PASSWORD);
        when(changePasswordService.createChangePasswordRequest(anyLong(), any(ChangePasswordRequest.class)))
                .thenReturn(
                        TokenModel.builder()
                                .token(token)
                                .build()
                );
        var changePasswordRequestStatusDto = ChangePasswordRequestStatusDto.builder()
                .token(token)
                .active(true)
                .build();
        mockMvc.perform(post(REQUEST_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(changePasswordRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(changePasswordRequestStatusDto)));
    }

    @Test
    void testConfirmChangePasswordRequestUnauthorized() throws Exception {
        mockMvc.perform(post(CONFIRM_URL)
                .param(TOKEN_PARAM, UUID.randomUUID().toString())
                .param(CONFIRMATION_CODE_PARAM, CONFIRMATION_CODE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testConfirmChangePasswordRequest() throws Exception {
        var changePasswordRequestEntity = createChangePasswordRequestEntity(CONFIRMATION_CODE);
        when(changePasswordService.confirmChangePassword(changePasswordRequestEntity.getToken(),
                CONFIRMATION_CODE)).thenReturn(changePasswordRequestEntity);
        mockMvc.perform(post(CONFIRM_URL)
                        .param(TOKEN_PARAM, changePasswordRequestEntity.getToken())
                        .param(CONFIRMATION_CODE_PARAM, CONFIRMATION_CODE)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk());
        verify(changePasswordService, atLeastOnce()).confirmChangePassword(changePasswordRequestEntity.getToken(),
                CONFIRMATION_CODE);
    }

    @Test
    void testConfirmChangePasswordRequestWithNullToken() throws Exception {
        mockMvc.perform(post(CONFIRM_URL)
                        .param(CONFIRMATION_CODE_PARAM, CONFIRMATION_CODE)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testConfirmChangePasswordRequestWithNullConfirmationCode() throws Exception {
        mockMvc.perform(post(CONFIRM_URL)
                        .param(TOKEN_PARAM, TOKEN_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetChangePasswordRequestStatusUnauthorized() throws Exception {
        mockMvc.perform(get(REQUEST_STATUS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetChangePasswordRequestStatusSuccess() throws Exception {
        var changePasswordRequestStatusDto = ChangePasswordRequestStatusDto.builder()
                .active(false)
                .build();
        when(changePasswordService.getChangePasswordRequestStatus(anyLong()))
                .thenReturn(changePasswordRequestStatusDto);
        mockMvc.perform(get(REQUEST_STATUS_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(changePasswordRequestStatusDto)));
    }
}
