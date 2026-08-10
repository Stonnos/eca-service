package com.ecaservice.notification.service;

import com.ecaservice.notification.AbstractJpaTest;
import com.ecaservice.notification.config.AppProperties;
import com.ecaservice.notification.config.WebPushProperties;
import com.ecaservice.notification.entity.MessageStatus;
import com.ecaservice.notification.entity.UserNotificationEntity;
import com.ecaservice.notification.exception.InvalidNotificationsIdsException;
import com.ecaservice.notification.mapping.NotificationMapperImpl;
import com.ecaservice.notification.repository.UserNotificationRepository;
import com.ecaservice.web.dto.model.ReadNotificationsDto;
import com.ecaservice.web.dto.model.SimplePageRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ecaservice.notification.TestHelperUtils.createNotificationEntity;
import static com.ecaservice.notification.TestHelperUtils.createUserPushNotificationRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserNotificationService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({UserNotificationService.class, AppProperties.class, WebPushProperties.class, NotificationMapperImpl.class})
class UserNotificationServiceTest extends AbstractJpaTest {

    private static final String CURRENT_USER = "currentUser";
    private static final String OTHER_USER = "otherUser";
    private static final int PAGE_SIZE = 25;

    @Autowired
    private WebPushProperties webPushProperties;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @MockBean
    private UserService userService;

    @Autowired
    private UserNotificationService userNotificationService;

    @Override
    public void init() {
        when(userService.getCurrentUser()).thenReturn(CURRENT_USER);
    }

    @Override
    public void deleteAll() {
        userNotificationRepository.deleteAll();
    }

    @Test
    void testSaveUserNotification() {
        var userPushNotificationRequest = createUserPushNotificationRequest();
        userNotificationService.save(userPushNotificationRequest);
        var notificationEntities = userNotificationRepository.findAll();
        assertThat(notificationEntities).hasSameSizeAs(userPushNotificationRequest.getReceivers());
        var notificationEntity = notificationEntities.iterator().next();
        assertThat(notificationEntity.getCreated()).isNotNull();
        assertThat(notificationEntity.getInitiator()).isEqualTo(userPushNotificationRequest.getInitiator());
        assertThat(notificationEntity.getMessageText()).isEqualTo(userPushNotificationRequest.getMessageText());
        assertThat(notificationEntity.getMessageType()).isEqualTo(userPushNotificationRequest.getMessageType());
        String expectedReceiver = userPushNotificationRequest.getReceivers().iterator().next();
        assertThat(notificationEntity.getReceiver()).isEqualTo(expectedReceiver);
        assertThat(notificationEntity.getMessageStatus()).isEqualTo(MessageStatus.NOT_READ);
        assertThat(notificationEntity.getParameters().size()).isEqualTo(
                userPushNotificationRequest.getAdditionalProperties().size());
    }

    @Test
    void testGetNotificationsPage() {
        var validNotifications = createAndSaveValidNotReadNotifications();
        createAndSaveInvalidNotificationsForPaging();
        var notificationsPage = userNotificationService.getNextPage(new SimplePageRequestDto(0, PAGE_SIZE));
        assertThat(notificationsPage).isNotNull();
        assertThat(notificationsPage.getTotalCount()).isEqualTo(validNotifications.size());
        assertThat(notificationsPage.getPage()).isZero();
        assertThat(notificationsPage.getContent()).hasSameSizeAs(validNotifications);
    }

    @Test
    void testGetNotReadNotificationsCount() {
        var validNotifications = createAndSaveValidNotReadNotifications();
        createAndSaveInvalidNotificationsForStatistics();
        var userNotificationStatisticsDto = userNotificationService.getNotificationStatistics();
        assertThat(userNotificationStatisticsDto.getNotReadCount()).isEqualTo(validNotifications.size());
    }

    @Test
    void testSuccessReadNotificationsIds() {
        var validNotificationsIds = createAndSaveValidNotReadNotificationsIds();
        createAndSaveInvalidNotificationsForRead();
        testReadNotifications(validNotificationsIds, validNotificationsIds);
    }

    @Test
    void testSuccessReadAllNotifications() {
        var validNotificationsIds = createAndSaveValidNotReadNotificationsIds();
        createAndSaveInvalidNotificationsForRead();
        testReadNotifications(validNotificationsIds, Collections.emptySet());
    }

    @Test
    void testReadInvalidNotifications() {
        var ids = createAndSaveValidNotReadNotificationsIds();
        var invalidNotificationIds = createAndSaveInvalidNotificationsForRead();
        ids.addAll(invalidNotificationIds);
        assertThrows(InvalidNotificationsIdsException.class, () ->
                userNotificationService.readNotifications(new ReadNotificationsDto(ids)));
    }

    private Set<Long> createAndSaveValidNotReadNotificationsIds() {
        return createAndSaveValidNotReadNotifications()
                .stream()
                .map(UserNotificationEntity::getId)
                .collect(Collectors.toSet());
    }

    private void testReadNotifications(Set<Long> validNotificationsIds, Set<Long> readIds) {
        userNotificationService.readNotifications(new ReadNotificationsDto(readIds));
        validNotificationsIds.forEach(id -> {
            var notification = userNotificationRepository.findById(id).orElse(null);
            assertThat(notification).isNotNull();
            assertThat(notification.getMessageStatus()).isEqualTo(MessageStatus.READ);
        });
    }

    private List<UserNotificationEntity> createAndSaveValidNotReadNotifications() {
        var notifications = List.of(
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ, LocalDateTime.now())
        );
        return userNotificationRepository.saveAll(notifications);
    }

    private Set<Long> createAndSaveInvalidNotificationsForRead() {
        var notifications = List.of(
                createNotificationEntity(OTHER_USER, MessageStatus.NOT_READ, LocalDateTime.now()),
                createNotificationEntity(OTHER_USER, MessageStatus.NOT_READ, LocalDateTime.now())
        );
        return userNotificationRepository.saveAll(notifications)
                .stream()
                .map(UserNotificationEntity::getId)
                .collect(Collectors.toSet());
    }

    private void createAndSaveInvalidNotificationsForPaging() {
        var notifications = List.of(
                createNotificationEntity(OTHER_USER, MessageStatus.NOT_READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ,
                        LocalDateTime.now().minusDays(webPushProperties.getNotificationLifeTimeDays() + 1))
        );
        userNotificationRepository.saveAll(notifications);
    }

    private void createAndSaveInvalidNotificationsForStatistics() {
        var notifications = List.of(
                createNotificationEntity(OTHER_USER, MessageStatus.NOT_READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ,
                        LocalDateTime.now().minusDays(webPushProperties.getNotificationLifeTimeDays() + 1))
        );
        userNotificationRepository.saveAll(notifications);
    }
}
