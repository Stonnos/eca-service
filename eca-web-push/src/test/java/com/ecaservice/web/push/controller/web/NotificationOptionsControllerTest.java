package com.ecaservice.web.push.controller.web;

import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.UserProfileNotificationOptionsDto;
import com.ecaservice.web.push.dto.UpdateUserNotificationOptionsDto;
import com.ecaservice.web.push.entity.NotificationOptionsEntity;
import com.ecaservice.web.push.service.NotificationOptionsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.ecaservice.web.push.TestHelperUtils.createUpdateUserNotificationOptionsDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link NotificationOptionsController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = NotificationOptionsController.class)
class NotificationOptionsControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/notification/options";

    @MockBean
    private NotificationOptionsService notificationOptionsService;

    @Test
    void testGetUserProfileNotificationOptionsUnauthorized() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserProfileNotificationOptionsOk() throws Exception {
        UserProfileNotificationOptionsDto expected = new UserProfileNotificationOptionsDto();
        when(notificationOptionsService.getUserNotificationOptions(anyString())).thenReturn(expected);
        mockMvc.perform(get(BASE_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testUpdateUserProfileNotificationOptionsUnauthorized() throws Exception {
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserNotificationOptionsDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUserProfileNotificationOptionsOk() throws Exception {
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        when(notificationOptionsService.updateUserNotificationOptions(anyString(),
                any(UpdateUserNotificationOptionsDto.class)))
                .thenReturn(new NotificationOptionsEntity());
        mockMvc.perform(put(BASE_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserNotificationOptionsDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserProfileNotificationOptionsWithEmptyEventType() throws Exception {
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        updateUserNotificationOptionsDto.getNotificationEventOptions().iterator().next().setEventType(null);
        mockMvc.perform(put(BASE_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserNotificationOptionsDto)))
                .andExpect(status().isBadRequest());
    }
}
