package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.oauth.dto.CreateResetPasswordRequest;
import com.ecaservice.oauth.dto.ResetPasswordRequest;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.repository.ResetPasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.service.ResetPasswordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.ecaservice.oauth.TestHelperUtils.createResetPasswordRequestEntity;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ResetPasswordController} functionality.
 *
 * @author Roman Batygin
 */
@EnableGlobalExceptionHandler
@WebMvcTest(controllers = ResetPasswordController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ResetPasswordController.class)
        })
@AutoConfigureMockMvc(addFilters = false)
class ResetPasswordControllerTest {

    private static final String BASE_URL = "/password";
    private static final String CREATE_RESET_PASSWORD_REQUEST_URL = BASE_URL + "/create-reset-request";
    private static final String RESET_URL = BASE_URL + "/reset";
    private static final String VERIFY_TOKEN_URL = BASE_URL + "/verify-token";
    private static final String EMAIL = "test@mail.ru";
    private static final String PASSWORD = "pa66word!";
    private static final String TOKEN_PARAM = "token";

    @MockBean
    private UserEntityRepository userEntityRepository;
    @MockBean
    private ResetPasswordRequestRepository resetPasswordRequestRepository;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;
    @MockBean
    private ResetPasswordService resetPasswordService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateResetPasswordRequestWithEmptyEmail() throws Exception {
        CreateResetPasswordRequest createResetPasswordRequest = new CreateResetPasswordRequest();
        mockMvc.perform(post(CREATE_RESET_PASSWORD_REQUEST_URL)
                .content(objectMapper.writeValueAsString(createResetPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateResetPasswordRequestWithNotExistsEmail() throws Exception {
        CreateResetPasswordRequest createResetPasswordRequest = new CreateResetPasswordRequest(EMAIL);
        when(userEntityRepository.existsByEmail(EMAIL)).thenReturn(false);
        mockMvc.perform(post(CREATE_RESET_PASSWORD_REQUEST_URL)
                .content(objectMapper.writeValueAsString(createResetPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateResetPasswordRequest() throws Exception {
        CreateResetPasswordRequest createResetPasswordRequest = new CreateResetPasswordRequest(EMAIL);
        when(userEntityRepository.existsByEmail(EMAIL)).thenReturn(true);
        when(resetPasswordService.createResetPasswordRequest(createResetPasswordRequest)).thenReturn(
                TokenModel.builder().build());
        mockMvc.perform(post(CREATE_RESET_PASSWORD_REQUEST_URL)
                .content(objectMapper.writeValueAsString(createResetPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testResetPasswordWithEmptyPassword() throws Exception {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setToken(UUID.randomUUID().toString());
        mockMvc.perform(post(RESET_URL)
                .content(objectMapper.writeValueAsString(resetPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testResetPasswordWithEmptyToken() throws Exception {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setPassword(PASSWORD);
        mockMvc.perform(post(RESET_URL)
                .content(objectMapper.writeValueAsString(resetPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testResetPassword() throws Exception {
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(UUID.randomUUID().toString(), PASSWORD);
        when(resetPasswordService.resetPassword(resetPasswordRequest)).thenReturn(createResetPasswordRequestEntity());
        mockMvc.perform(post(RESET_URL)
                .content(objectMapper.writeValueAsString(resetPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(resetPasswordService, atLeastOnce()).resetPassword(resetPasswordRequest);
    }

    @Test
    void testVerifyNullToken() throws Exception {
        mockMvc.perform(post(VERIFY_TOKEN_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerifyTokenOk() throws Exception {
        String token = UUID.randomUUID().toString();
        mockMvc.perform(post(VERIFY_TOKEN_URL)
                .param(TOKEN_PARAM, token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
