package com.ecaservice.web.push.service;

import com.ecaservice.web.dto.model.ReadNotificationsDto;
import com.ecaservice.web.dto.model.SimplePageRequestDto;
import com.ecaservice.web.push.AbstractJpaTest;
import com.ecaservice.web.push.config.AppProperties;
import com.ecaservice.web.push.entity.MessageStatus;
import com.ecaservice.web.push.entity.NotificationEntity;
import com.ecaservice.web.push.exception.InvalidNotificationsException;
import com.ecaservice.web.push.mapping.NotificationMapperImpl;
import com.ecaservice.web.push.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.web.push.TestHelperUtils.createNotificationEntity;
import static com.ecaservice.web.push.TestHelperUtils.createUserPushNotificationRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void init() {
        when(userService.getCurrentUser()).thenReturn(CURRENT_USER);
    }

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
        testReadNotifications(validNotificationsIds, Collections.emptyList());
    }

    @Test
    void testReadInvalidNotifications() {
        var ids = createAndSaveValidNotReadNotificationsIds();
        var invalidNotificationIds = createAndSaveInvalidNotificationsForRead();
        ids.addAll(invalidNotificationIds);
        assertThrows(InvalidNotificationsException.class, () ->
                userNotificationService.readNotifications(
                        ReadNotificationsDto.builder()
                                .ids(ids)
                                .build()
                ));
    }

    private List<Long> createAndSaveValidNotReadNotificationsIds() {
        return createAndSaveValidNotReadNotifications()
                .stream()
                .map(NotificationEntity::getId)
                .collect(Collectors.toList());
    }

    private void testReadNotifications(List<Long> validNotificationsIds, List<Long> readIds) {
        userNotificationService.readNotifications(
                ReadNotificationsDto.builder()
                        .ids(readIds)
                        .build()
        );
        validNotificationsIds.forEach(id -> {
            var notification = notificationRepository.findById(id).orElse(null);
            assertThat(notification).isNotNull();
            assertThat(notification.getMessageStatus()).isEqualTo(MessageStatus.READ);
        });
    }

    private List<NotificationEntity> createAndSaveValidNotReadNotifications() {
        var notifications = List.of(
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ, LocalDateTime.now())
        );
        return notificationRepository.saveAll(notifications);
    }

    private List<Long> createAndSaveInvalidNotificationsForRead() {
        var notifications = List.of(
                createNotificationEntity(OTHER_USER, MessageStatus.NOT_READ, LocalDateTime.now()),
                createNotificationEntity(OTHER_USER, MessageStatus.NOT_READ, LocalDateTime.now())
        );
        return notificationRepository.saveAll(notifications)
                .stream()
                .map(NotificationEntity::getId)
                .collect(Collectors.toList());
    }

    private void createAndSaveInvalidNotificationsForPaging() {
        var notifications = List.of(
                createNotificationEntity(OTHER_USER, MessageStatus.NOT_READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ,
                        LocalDateTime.now().minusDays(appProperties.getNotificationLifeTimeDays() + 1))
        );
        notificationRepository.saveAll(notifications);
    }

    private void createAndSaveInvalidNotificationsForStatistics() {
        var notifications = List.of(
                createNotificationEntity(OTHER_USER, MessageStatus.NOT_READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.READ, LocalDateTime.now()),
                createNotificationEntity(CURRENT_USER, MessageStatus.NOT_READ,
                        LocalDateTime.now().minusDays(appProperties.getNotificationLifeTimeDays() + 1))
        );
        notificationRepository.saveAll(notifications);
    }
}
