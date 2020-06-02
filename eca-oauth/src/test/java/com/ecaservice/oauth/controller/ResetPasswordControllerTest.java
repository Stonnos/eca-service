package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.dto.ForgotPasswordRequest;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.service.ResetPasswordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import static com.ecaservice.oauth.TestHelperUtils.createResetPasswordRequestEntity;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ResetPasswordController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = ResetPasswordController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ResetPasswordController.class)
        })
@AutoConfigureMockMvc(addFilters = false)
class ResetPasswordControllerTest {

    private static final String BASE_URL = "/password";
    private static final String FORGOT_URL = BASE_URL + "/forgot";
    private static final String EMAIL = "test@mail.ru";

    @MockBean
    private UserEntityRepository userEntityRepository;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;
    @MockBean
    private ResetPasswordService resetPasswordService;

    @Inject
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testForgotPasswordWithEmptyEmail() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        mockMvc.perform(post(FORGOT_URL)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testForgotPasswordWithNotExistsEmail() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(EMAIL);
        when(userEntityRepository.existsByEmail(EMAIL)).thenReturn(false);
        mockMvc.perform(post(FORGOT_URL)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testForgotPassword() throws Exception {
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(EMAIL);
        when(userEntityRepository.existsByEmail(EMAIL)).thenReturn(true);
        when(resetPasswordService.getOrSaveResetPasswordRequest(forgotPasswordRequest)).thenReturn(
                createResetPasswordRequestEntity());
        mockMvc.perform(post(FORGOT_URL)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
