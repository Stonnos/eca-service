package com.ecaservice.oauth.controller;

import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.service.ChangePasswordService;
import com.ecaservice.oauth.service.Oauth2TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
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

import static com.ecaservice.oauth.TestHelperUtils.createChangePasswordRequestEntity;
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
@WebMvcTest(controllers = ChangePasswordController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ChangePasswordController.class)
        })
@AutoConfigureMockMvc(addFilters = false)
class ChangePasswordControllerTest {

    private static final String TOKEN_PARAM = "token";
    private static final String TOKEN_VALUE = "tokenValue";

    private static final String BASE_URL = "/password/change";
    private static final String CONFIRM_URL = BASE_URL + "/confirm";
    private static final String REQUEST_URL = BASE_URL + "/request";
    private static final String PASSWORD = "pa66word!";

    @MockBean
    private ChangePasswordService changePasswordService;
    @MockBean
    private Oauth2TokenService oauth2TokenService;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @Inject
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateChangePasswordRequestWithEmptyOldPassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(StringUtils.EMPTY, PASSWORD);
        mockMvc.perform(post(REQUEST_URL)
                .content(objectMapper.writeValueAsString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateChangePasswordRequestWithEmptyNewPassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(PASSWORD, StringUtils.EMPTY);
        mockMvc.perform(post(REQUEST_URL)
                .content(objectMapper.writeValueAsString(changePasswordRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testApproveChangePasswordRequest() throws Exception {
        ChangePasswordRequestEntity changePasswordRequestEntity = createChangePasswordRequestEntity();
        when(changePasswordService.changePassword(TOKEN_VALUE)).thenReturn(changePasswordRequestEntity);
        mockMvc.perform(post(CONFIRM_URL)
                .param(TOKEN_PARAM, TOKEN_VALUE))
                .andExpect(status().isOk());
        verify(changePasswordService, atLeastOnce()).changePassword(TOKEN_VALUE);
        verify(oauth2TokenService, atLeastOnce()).revokeTokens(changePasswordRequestEntity.getUserEntity());
    }

    @Test
    void testApproveChangePasswordRequestWithNullToken() throws Exception {
        mockMvc.perform(post(CONFIRM_URL))
                .andExpect(status().isBadRequest());
    }
}
