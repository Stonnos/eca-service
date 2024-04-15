package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.service.ChangeEmailService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.ChangeEmailRequestStatusDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.ecaservice.oauth.TestHelperUtils.createChangeEmailRequestEntity;
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
 * Unit tests for checking {@link ChangeEmailController} functionality.
 *
 * @author Roman Batygin
 */
@EnableGlobalExceptionHandler
@WebMvcTest(controllers = ChangeEmailController.class)
class ChangeEmailControllerTest extends AbstractControllerTest {

    private static final String TOKEN_PARAM = "token";
    private static final String CONFIRMATION_CODE_PARAM = "confirmationCode";
    private static final String TOKEN_VALUE = "tokenValue";
    private static final String CONFIRMATION_CODE = "code";

    private static final String BASE_URL = "/email/change";
    private static final String CONFIRM_URL = BASE_URL + "/confirm";
    private static final String REQUEST_URL = BASE_URL + "/request";
    private static final String REQUEST_STATUS_URL = BASE_URL + "/request-status";
    private static final String EMAIL = "test@mail.ru";
    private static final String NEW_EMAIL_PARAM = "newEmail";
    private static final String INVALID_EMAIL = "123";

    @MockBean
    private ChangeEmailService changeEmailService;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateChangeEmailRequestWithEmptyEmail() throws Exception {
        mockMvc.perform(post(REQUEST_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateChangeEmailRequestWithInvalidNewEmail() throws Exception {
        mockMvc.perform(post(REQUEST_URL)
                .param(NEW_EMAIL_PARAM, INVALID_EMAIL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateChangeEmailUnauthorized() throws Exception {
        mockMvc.perform(post(REQUEST_URL)
                .param(NEW_EMAIL_PARAM, EMAIL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateChangeEmailRequestOk() throws Exception {
        String token = UUID.randomUUID().toString();
        var changeEmailRequestStatusDto = ChangeEmailRequestStatusDto.builder()
                .token(token)
                .newEmail(EMAIL)
                .active(true)
                .build();
        when(changeEmailService.createChangeEmailRequest(anyLong(), any(String.class)))
                .thenReturn(TokenModel.builder()
                        .token(token)
                        .email(EMAIL)
                        .build()
                );
        mockMvc.perform(post(REQUEST_URL)
                .param(NEW_EMAIL_PARAM, EMAIL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(changeEmailRequestStatusDto)));
    }

    @Test
    void testConfirmChangeEmailUnauthorized() throws Exception {
        mockMvc.perform(post(CONFIRM_URL)
                .param(TOKEN_PARAM, TOKEN_VALUE)
                .param(CONFIRMATION_CODE_PARAM, CONFIRMATION_CODE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testConfirmChangeEmailRequest() throws Exception {
        var changeEmailRequestEntity = createChangeEmailRequestEntity(TOKEN_VALUE);
        when(changeEmailService.confirmChangeEmail(TOKEN_VALUE, CONFIRMATION_CODE)).thenReturn(
                changeEmailRequestEntity);
        mockMvc.perform(post(CONFIRM_URL)
                .param(TOKEN_PARAM, TOKEN_VALUE)
                .param(CONFIRMATION_CODE_PARAM, CONFIRMATION_CODE)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk());
        verify(changeEmailService, atLeastOnce()).confirmChangeEmail(TOKEN_VALUE, CONFIRMATION_CODE);
    }

    @Test
    void testConfirmChangeEmailRequestWithNullToken() throws Exception {
        mockMvc.perform(post(CONFIRM_URL)
                .param(CONFIRMATION_CODE_PARAM, CONFIRMATION_CODE)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testConfirmChangeEmailRequestWithNullConfirmationCode() throws Exception {
        mockMvc.perform(post(CONFIRM_URL)
                        .param(TOKEN_PARAM, TOKEN_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetChangeEmailRequestStatusUnauthorized() throws Exception {
        mockMvc.perform(get(REQUEST_STATUS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetChangeEmailRequestStatusSuccess() throws Exception {
        var changeEmailRequestStatusDto = ChangeEmailRequestStatusDto.builder()
                .active(false)
                .build();
        when(changeEmailService.getChangeEmailRequestStatus(anyLong())).thenReturn(changeEmailRequestStatusDto);
        mockMvc.perform(get(REQUEST_STATUS_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(changeEmailRequestStatusDto)));
    }
}
