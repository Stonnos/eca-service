package com.ecaservice.web.push.controller.web;

import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.ReadNotificationsDto;
import com.ecaservice.web.dto.model.SimplePageRequestDto;
import com.ecaservice.web.dto.model.UserNotificationStatisticsDto;
import com.ecaservice.web.push.service.UserNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.ecaservice.web.dto.util.FieldConstraints.READ_NOTIFICATIONS_LIST_MAX_LENGTH;
import static com.ecaservice.web.push.TestHelperUtils.createUserNotificationDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link UserNotificationController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = UserNotificationController.class)
class UserNotificationControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/notifications";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String STATISTICS_URL = BASE_URL + "/statistics";
    private static final String READ_NOTIFICATIONS_URL = BASE_URL + "/read";

    private static final long TOTAL_ELEMENTS = 1L;
    private static final int PAGE_NUMBER = 0;

    @MockBean
    private UserNotificationService userNotificationService;

    @Test
    void testGetNotificationsPageUnauthorized() throws Exception {
        mockMvc.perform(post(LIST_URL)
                .content(objectMapper.writeValueAsString(new SimplePageRequestDto(0, 1)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetNotificationsPage() throws Exception {
        var userNotifications = Collections.singletonList(createUserNotificationDto());
        var expected = PageDto.of(userNotifications, PAGE_NUMBER, TOTAL_ELEMENTS);
        when(userNotificationService.getNextPage(any(SimplePageRequestDto.class))).thenReturn(expected);
        mockMvc.perform(post(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(new SimplePageRequestDto(0, 1)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testGetNotificationsStatisticsUnauthorized() throws Exception {
        mockMvc.perform(get(STATISTICS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetNotificationsStatisticsOk() throws Exception {
        var notificationStatisticsDto = UserNotificationStatisticsDto.builder().build();
        when(userNotificationService.getNotificationStatistics()).thenReturn(notificationStatisticsDto);
        mockMvc.perform(get(STATISTICS_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(notificationStatisticsDto)));
    }

    @Test
    void testReadNotificationsUnauthorized() throws Exception {
        ReadNotificationsDto readNotificationsDto = new ReadNotificationsDto();
        mockMvc.perform(post(READ_NOTIFICATIONS_URL)
                .content(objectMapper.writeValueAsString(readNotificationsDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testReadNotificationsOk() throws Exception {
        ReadNotificationsDto readNotificationsDto = new ReadNotificationsDto();
        mockMvc.perform(post(READ_NOTIFICATIONS_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(readNotificationsDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testReadNotificationsWithInvalidSize() throws Exception {
        ReadNotificationsDto readNotificationsDto = new ReadNotificationsDto();
        Set<Long> ids =  LongStream.range(0, READ_NOTIFICATIONS_LIST_MAX_LENGTH + 1)
                .boxed()
                .collect(Collectors.toSet());
        readNotificationsDto.setIds(ids);
        mockMvc.perform(post(READ_NOTIFICATIONS_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(readNotificationsDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
