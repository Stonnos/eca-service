package com.ecaservice.web.push.service;

import com.ecaservice.web.dto.model.SimplePageRequestDto;
import com.ecaservice.web.push.AbstractJpaTest;
import com.ecaservice.web.push.config.AppProperties;
import com.ecaservice.web.push.entity.MessageStatus;
import com.ecaservice.web.push.entity.NotificationEntity;
import com.ecaservice.web.push.mapping.NotificationMapperImpl;
import com.ecaservice.web.push.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.web.push.TestHelperUtils.createNotificationEntity;
import static com.ecaservice.web.push.TestHelperUtils.createUserPushNotificationRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserNotificationService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({UserNotificationService.class, AppProperties.class, NotificationMapperImpl.class})
class UserNotificationServiceTest extends AbstractJpaTest {

    private static final String CURRENT_USER = "currentUser";
    private static final String OTHER_USER = "otherUser";
    private static final int PAGE_SIZE = 25;

    @Inject
    private AppProperties appProperties;
    @Inject
    private NotificationRepository notificationRepository;
    @MockBean
    private UserService userService;

    @Inject
    private UserNotificationService userNotificationService;

    @Override
    public void deleteAll() {
        notificationRepository.deleteAll();
    }

    @Test
    void testSaveUserNotification() {
        var userPushNotificationRequest = createUserPushNotificationRequest();
        userNotificationService.save(userPushNotificationRequest);
        var notificationEntities = notificationRepository.findAll();
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
        var validNotifications = createAndSaveValidNotifications();
        createAndSaveInvalidNotifications();
        when(userService.getCurrentUser()).thenReturn(CURRENT_USER);
        var notificationsPage = userNotificationService.getNextPage(new SimplePageRequestDto(0, PAGE_SIZE));
        assertThat(notificationsPage).isNotNull();
        assertThat(notificationsPage.getTotalCount()).isEqualTo(validNotifications.size());
        assertThat(notificationsPage.getPage()).isZero();
        assertThat(notificationsPage.getContent()).hasSameSizeAs(validNotifications);
    }

    @Test
    void testGetNotReadNotificationsCount() {
        var validNotifications = createAndSaveValidNotifications();
        createAndSaveInvalidNotificationsForCount();
        when(userService.getCurrentUser()).thenReturn(CURRENT_USER);
        long notReadCount = userNotificationService.getNotReadNotificationsCount();
        assertThat(notReadCount).isEqualTo(validNotifications.size());
    }

    private List<NotificationEntity> createAndSaveValidNotifications() {
        var notifications = List.of(
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ, LocalDateTime.now())
        );
        return notificationRepository.saveAll(notifications);
    }

    private void createAndSaveInvalidNotifications() {
        var notifications = List.of(
                createNotificationEntity(OTHER_USER, MessageStatus.NOT_READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ,
                        LocalDateTime.now().minusDays(appProperties.getNotificationLifeTimeDays() + 1))
        );
        notificationRepository.saveAll(notifications);
    }

    private void createAndSaveInvalidNotificationsForCount() {
        var notifications = List.of(
                createNotificationEntity(OTHER_USER, MessageStatus.NOT_READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ,
                        LocalDateTime.now().minusDays(appProperties.getNotificationLifeTimeDays() + 1))
        );
        notificationRepository.saveAll(notifications);
    }
}
