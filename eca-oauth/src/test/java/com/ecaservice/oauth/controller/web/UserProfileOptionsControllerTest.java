package com.ecaservice.oauth.controller.web;

import com.ecaservice.oauth.dto.UpdateUserNotificationOptionsDto;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.oauth.mapping.RoleMapperImpl;
import com.ecaservice.oauth.mapping.UserMapperImpl;
import com.ecaservice.oauth.service.UserProfileOptionsService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.UserProfileNotificationOptionsDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.ecaservice.oauth.TestHelperUtils.createUpdateUserNotificationOptionsDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link UserProfileOptionsController} functionality.
 *
 * @author Roman Batygin
 */
@Disabled
@WebMvcTest(controllers = UserProfileOptionsController.class)
@Import({UserMapperImpl.class, RoleMapperImpl.class})
class UserProfileOptionsControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/user/profile/options";
    private static final String GET_USER_PROFILE_NOTIFICATIONS_URL = BASE_URL + "/notifications";
    private static final String UPDATE_USER_PROFILE_NOTIFICATIONS_URL = BASE_URL + "/update-notifications";

    @MockBean
    private UserProfileOptionsService userProfileOptionsService;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void testGetUserProfileNotificationOptionsUnauthorized() throws Exception {
        mockMvc.perform(get(GET_USER_PROFILE_NOTIFICATIONS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUserProfileNotificationOptionsOk() throws Exception {
        UserProfileNotificationOptionsDto expected = new UserProfileNotificationOptionsDto();
        when(userProfileOptionsService.getUserNotificationOptions(anyString())).thenReturn(expected);
        mockMvc.perform(get(GET_USER_PROFILE_NOTIFICATIONS_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testUpdateUserProfileNotificationOptionsUnauthorized() throws Exception {
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        mockMvc.perform(put(UPDATE_USER_PROFILE_NOTIFICATIONS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserNotificationOptionsDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUserProfileNotificationOptionsOk() throws Exception {
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        when(userProfileOptionsService.updateUserNotificationOptions(anyString(),
                any(UpdateUserNotificationOptionsDto.class)))
                .thenReturn(new UserProfileOptionsEntity());
        mockMvc.perform(put(UPDATE_USER_PROFILE_NOTIFICATIONS_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserNotificationOptionsDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserProfileNotificationOptionsWithEmptyEventType() throws Exception {
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        updateUserNotificationOptionsDto.getNotificationEventOptions().iterator().next().setEventType(null);
        mockMvc.perform(put(UPDATE_USER_PROFILE_NOTIFICATIONS_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserNotificationOptionsDto)))
                .andExpect(status().isBadRequest());
    }
}
