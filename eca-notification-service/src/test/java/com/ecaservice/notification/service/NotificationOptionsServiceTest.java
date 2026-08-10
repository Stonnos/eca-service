package com.ecaservice.notification.service;

import com.ecaservice.core.lock.config.CoreLockAutoConfiguration;
import com.ecaservice.core.lock.metrics.LockMeterService;
import com.ecaservice.notification.AbstractJpaTest;
import com.ecaservice.notification.config.AppProperties;
import com.ecaservice.notification.config.UserNotificationProperties;
import com.ecaservice.notification.entity.NotificationEventType;
import com.ecaservice.notification.exception.DuplicateNotificationEventToUpdateException;
import com.ecaservice.notification.exception.NotificationEventNotFoundException;
import com.ecaservice.notification.mapping.NotificationOptionsMapperImpl;
import com.ecaservice.notification.repository.NotificationEventOptionsRepository;
import com.ecaservice.notification.repository.NotificationOptionsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ecaservice.notification.TestHelperUtils.createUpdateUserNotificationEventOptionsDto;
import static com.ecaservice.notification.TestHelperUtils.createUpdateUserNotificationOptionsDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

/**
 * Unit tests for checking {@link NotificationOptionsService} functionality.
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@Import({AppProperties.class, NotificationOptionsDataService.class, NotificationOptionsService.class,
        NotificationOptionsMapperImpl.class, UserNotificationProperties.class, CoreLockAutoConfiguration.class,
        UserNotificationOptionsConfigurationService.class})
class NotificationOptionsServiceTest extends AbstractJpaTest {

    private static final int NUM_THREADS = 2;
    private static final String USER = "admin";

    @MockBean
    private LockMeterService lockMeterService;

    @Autowired
    private UserNotificationProperties userNotificationProperties;

    @Autowired
    private NotificationOptionsRepository notificationOptionsRepository;
    @Autowired
    private NotificationEventOptionsRepository notificationEventOptionsRepository;

    @Autowired
    private NotificationOptionsService notificationOptionsService;

    @Override
    public void deleteAll() {
        notificationEventOptionsRepository.deleteAll();
        notificationOptionsRepository.deleteAll();
    }

    @Test
    void testCreateNewUserNotificationOptions() {
        var notificationOptionsDto = notificationOptionsService.getUserNotificationOptions(USER);
        assertThat(notificationOptionsDto).isNotNull();
        assertThat(notificationOptionsRepository.count()).isOne();
        var notificationOptions = notificationOptionsRepository.findByUser(USER);
        assertThat(notificationOptions).isNotNull();
        assertThat(notificationOptions.getCreated()).isNotNull();
        assertThat(notificationOptions.isEmailEnabled()).isEqualTo(userNotificationProperties.isEmailEnabled());
        assertThat(notificationOptions.isWebPushEnabled()).isEqualTo(userNotificationProperties.isWebPushEnabled());
        for (int i = 0; i < notificationOptionsDto.getNotificationEventOptions().size(); i++) {
            var actual = notificationOptionsDto.getNotificationEventOptions().get(i);
            var expected = notificationOptions.getNotificationEventOptions().get(i);
            assertThat(actual.isEmailSupported()).isEqualTo(expected.isEmailSupported());
            assertThat(actual.isWebPushSupported()).isEqualTo(expected.isWebPushSupported());
            assertThat(actual.isEmailEnabled()).isEqualTo(expected.isEmailEnabled());
            assertThat(actual.isWebPushEnabled()).isEqualTo(expected.isWebPushEnabled());
            assertThat(actual.getEventType()).isEqualTo(expected.getEventType().name());
        }
    }

    @Test
    void testGetUserNotificationOptionsExists() {
        var notificationOptionsDto = notificationOptionsService.getUserNotificationOptions(USER);
        assertThat(notificationOptionsDto).isNotNull();
        notificationOptionsDto = notificationOptionsService.getUserNotificationOptions(USER);
        assertThat(notificationOptionsDto).isNotNull();
        assertThat(notificationOptionsRepository.count()).isOne();
    }

    @Test
    void testGetUserNotificationOptions() {
        var notificationOptionsDto = notificationOptionsService.getUserNotificationOptions(USER);
        assertThat(notificationOptionsDto).isNotNull();
        assertThat(notificationOptionsRepository.count()).isOne();
        var notificationOptions = notificationOptionsRepository.findByUser(USER);
        assertThat(notificationOptions).isNotNull();
        assertThat(notificationOptions.getCreated()).isNotNull();
        assertThat(notificationOptions.isEmailEnabled()).isEqualTo(userNotificationProperties.isEmailEnabled());
        assertThat(notificationOptions.isWebPushEnabled()).isEqualTo(userNotificationProperties.isWebPushEnabled());
        for (int i = 0; i < notificationOptionsDto.getNotificationEventOptions().size(); i++) {
            var actual = notificationOptionsDto.getNotificationEventOptions().get(i);
            var expected = notificationOptions.getNotificationEventOptions().get(i);
            assertThat(actual.isEmailSupported()).isEqualTo(expected.isEmailSupported());
            assertThat(actual.isWebPushSupported()).isEqualTo(expected.isWebPushSupported());
            assertThat(actual.isEmailEnabled()).isEqualTo(expected.isEmailEnabled());
            assertThat(actual.isWebPushEnabled()).isEqualTo(expected.isWebPushEnabled());
            assertThat(actual.getEventType()).isEqualTo(expected.getEventType().name());
        }
    }

    @Test
    void testGetOrCreateOptionsInMultiThreadEnvironment() throws Exception {
        var hasError = new AtomicBoolean();
        final CountDownLatch finishedLatch = new CountDownLatch(NUM_THREADS);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    notificationOptionsService.getUserNotificationOptions(USER);
                } catch (Exception ex) {
                    hasError.set(true);
                } finally {
                    finishedLatch.countDown();
                }
            });
        }
        finishedLatch.await();
        executorService.shutdownNow();
        assertThat(hasError.get()).isFalse();
        assertThat(notificationOptionsRepository.count()).isOne();
    }

    @Test
    void testUpdateNotificationOptions() {
        var notificationOptionsDto = notificationOptionsService.getUserNotificationOptions(USER);
        assertThat(notificationOptionsDto.isEmailEnabled()).isTrue();
        assertThat(notificationOptionsDto.isWebPushEnabled()).isTrue();
        //Creates update data
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        //Update options
        notificationOptionsService.updateUserNotificationOptions(USER, updateUserNotificationOptionsDto);
        //Verify data updated
        var actualProfileOptions = notificationOptionsRepository.findByUser(USER);
        assertThat(actualProfileOptions).isNotNull();
        assertThat(actualProfileOptions.isWebPushEnabled()).isFalse();
        assertThat(actualProfileOptions.isEmailEnabled()).isFalse();
        actualProfileOptions.getNotificationEventOptions().forEach(userNotificationEventOptionsEntity -> {
            assertThat(userNotificationEventOptionsEntity.isEmailEnabled()).isFalse();
            assertThat(userNotificationEventOptionsEntity.isWebPushEnabled()).isFalse();
        });
    }

    @Test
    void testUpdateNotificationOptionsWithDuplicateEventCodes() {
        notificationOptionsService.getUserNotificationOptions(USER);
        //Creates update data
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        updateUserNotificationOptionsDto.getNotificationEventOptions().add(
                createUpdateUserNotificationEventOptionsDto(NotificationEventType.CLASSIFIER_STATUS_CHANGE));
        assertThrows(DuplicateNotificationEventToUpdateException.class,
                () -> notificationOptionsService.updateUserNotificationOptions(USER, updateUserNotificationOptionsDto));
    }

    @Test
    void testUpdateNotificationOptionsWithNotFoundEvent() {
        notificationOptionsService.getUserNotificationOptions(USER);
        notificationEventOptionsRepository.deleteAll();
        //Creates update data
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        assertThrows(NotificationEventNotFoundException.class,
                () -> notificationOptionsService.updateUserNotificationOptions(USER, updateUserNotificationOptionsDto));
    }
}
