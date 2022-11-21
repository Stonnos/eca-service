package com.ecaservice.web.push.controller.web;

import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.PushTokenDto;
import com.ecaservice.web.push.service.PushTokenService;
import com.ecaservice.web.push.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link PushTokenController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = PushTokenController.class)
class PushTokenControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/push/token";

    private static final String USER = "user";

    @MockBean
    private UserService userService;
    @MockBean
    private PushTokenService pushTokenService;

    @Test
    void testObtainTokenUnauthorized() throws Exception {
        mockMvc.perform(post(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testObtainTokenOk() throws Exception {
        var pushTokenDto = PushTokenDto.builder()
                .tokenId(UUID.randomUUID().toString())
                .build();
        when(userService.getCurrentUser()).thenReturn(USER);
        when(pushTokenService.obtainToken(USER)).thenReturn(pushTokenDto);
        mockMvc.perform(post(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(pushTokenDto)));
    }
}
