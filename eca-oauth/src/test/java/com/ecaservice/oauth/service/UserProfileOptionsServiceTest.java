package com.ecaservice.oauth.service;

import com.ecaservice.core.lock.config.CoreLockAutoConfiguration;
import com.ecaservice.core.lock.metrics.LockMeterService;
import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.config.UserProfileProperties;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.DuplicateNotificationEventToUpdateException;
import com.ecaservice.oauth.exception.NotificationEventNotFoundException;
import com.ecaservice.oauth.mapping.UserProfileOptionsMapperImpl;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.repository.UserNotificationEventOptionsRepository;
import com.ecaservice.oauth.repository.UserProfileOptionsRepository;
import com.ecaservice.user.profile.options.dto.UserNotificationEventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ecaservice.oauth.TestHelperUtils.createUpdateUserNotificationEventOptionsDto;
import static com.ecaservice.oauth.TestHelperUtils.createUpdateUserNotificationOptionsDto;
import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

/**
 * Unit tests for checking {@link UserProfileOptionsService} functionality.
 *
 * @author Roman Batygin
 */
@EnableAspectJAutoProxy
@Import({AppProperties.class, UserProfileOptionsDataService.class, UserProfileOptionsService.class,
        UserProfileOptionsMapperImpl.class, UserProfileProperties.class, CoreLockAutoConfiguration.class,
        UserProfileOptionsConfigurationService.class})
class UserProfileOptionsServiceTest extends AbstractJpaTest {

    private static final int NUM_THREADS = 2;

    @MockBean
    private LockMeterService lockMeterService;
    @MockBean
    private UserProfileOptionsDataEventService userProfileOptionsDataEventService;

    @Autowired
    private UserProfileProperties userProfileProperties;

    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private UserProfileOptionsRepository userProfileOptionsRepository;
    @Autowired
    private UserNotificationEventOptionsRepository userNotificationEventOptionsRepository;

    @Autowired
    private UserProfileOptionsService userProfileOptionsService;

    private UserEntity userEntity;

    @Override
    public void init() {
        userEntity = createAndSaveUser();
    }

    @Override
    public void deleteAll() {
        userNotificationEventOptionsRepository.deleteAll();
        userProfileOptionsRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Test
    void testCreateNewUserProfileOptions() {
        var userProfileOptionsDto = userProfileOptionsService.getUserProfileOptions(userEntity.getLogin());
        assertThat(userProfileOptionsDto).isNotNull();
        assertThat(userProfileOptionsRepository.count()).isOne();
        var userProfileOptions = userProfileOptionsRepository.findByUserEntity(userEntity);
        assertThat(userProfileOptions).isNotNull();
        assertThat(userProfileOptions.getCreated()).isNotNull();
        assertThat(userProfileOptions.isEmailEnabled()).isEqualTo(userProfileProperties.isEmailEnabled());
        assertThat(userProfileOptions.isWebPushEnabled()).isEqualTo(userProfileProperties.isWebPushEnabled());
        for (int i = 0; i < userProfileOptionsDto.getNotificationEventOptions().size(); i++) {
            var actual = userProfileOptionsDto.getNotificationEventOptions().get(i);
            var expected = userProfileOptions.getNotificationEventOptions().get(i);
            assertThat(actual.isEmailSupported()).isEqualTo(expected.isEmailSupported());
            assertThat(actual.isWebPushSupported()).isEqualTo(expected.isWebPushSupported());
            assertThat(actual.isEmailEnabled()).isEqualTo(expected.isEmailEnabled());
            assertThat(actual.isWebPushEnabled()).isEqualTo(expected.isWebPushEnabled());
            assertThat(actual.getEventType()).isEqualTo(expected.getEventType());
        }
    }

    @Test
    void testGetUserProfileOptions() {
        var userProfileOptionsDto = userProfileOptionsService.getUserProfileOptions(userEntity.getLogin());
        assertThat(userProfileOptionsDto).isNotNull();
        userProfileOptionsDto = userProfileOptionsService.getUserProfileOptions(userEntity.getLogin());
        assertThat(userProfileOptionsDto).isNotNull();
        assertThat(userProfileOptionsRepository.count()).isOne();
    }

    @Test
    void testGetUserProfileNotificationOptions() {
        var userNotificationOptions = userProfileOptionsService.getUserNotificationOptions(userEntity.getLogin());
        assertThat(userNotificationOptions).isNotNull();
        assertThat(userProfileOptionsRepository.count()).isOne();
        var userProfileOptions = userProfileOptionsRepository.findByUserEntity(userEntity);
        assertThat(userProfileOptions).isNotNull();
        assertThat(userProfileOptions.getCreated()).isNotNull();
        assertThat(userProfileOptions.isEmailEnabled()).isEqualTo(userProfileProperties.isEmailEnabled());
        assertThat(userProfileOptions.isWebPushEnabled()).isEqualTo(userProfileProperties.isWebPushEnabled());
        for (int i = 0; i < userNotificationOptions.getNotificationEventOptions().size(); i++) {
            var actual = userNotificationOptions.getNotificationEventOptions().get(i);
            var expected = userProfileOptions.getNotificationEventOptions().get(i);
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
                    userProfileOptionsService.getUserProfileOptions(userEntity.getLogin());
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
        assertThat(userProfileOptionsRepository.count()).isOne();
    }

    @Test
    void testUpdateNotificationOptions() {
        var userProfileOptionsDto = userProfileOptionsService.getUserProfileOptions(userEntity.getLogin());
        assertThat(userProfileOptionsDto.isEmailEnabled()).isTrue();
        assertThat(userProfileOptionsDto.isWebPushEnabled()).isTrue();
        //Creates update data
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        //Update options
        userProfileOptionsService.updateUserNotificationOptions(userEntity.getLogin(),
                updateUserNotificationOptionsDto);
        //Verify data updated
        var actualProfileOptions = userProfileOptionsRepository.findByUserEntity(userEntity);
        assertThat(actualProfileOptions).isNotNull();
        assertThat(actualProfileOptions.isWebPushEnabled()).isFalse();
        assertThat(actualProfileOptions.isEmailEnabled()).isFalse();
        assertThat(actualProfileOptions.getVersion()).isEqualTo(userProfileOptionsDto.getVersion() + 1);
        actualProfileOptions.getNotificationEventOptions().forEach(userNotificationEventOptionsEntity -> {
            assertThat(userNotificationEventOptionsEntity.isEmailEnabled()).isFalse();
            assertThat(userNotificationEventOptionsEntity.isWebPushEnabled()).isFalse();
        });
    }

    @Test
    void testUpdateNotificationOptionsWithDuplicateEventCodes() {
        userProfileOptionsService.getUserProfileOptions(userEntity.getLogin());
        //Creates update data
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        updateUserNotificationOptionsDto.getNotificationEventOptions().add(
                createUpdateUserNotificationEventOptionsDto(UserNotificationEventType.CLASSIFIER_STATUS_CHANGE));
        assertThrows(DuplicateNotificationEventToUpdateException.class,
                () -> userProfileOptionsService.updateUserNotificationOptions(userEntity.getLogin(),
                        updateUserNotificationOptionsDto));
    }

    @Test
    void testUpdateNotificationOptionsWithNotFoundEvent() {
        userProfileOptionsService.getUserProfileOptions(userEntity.getLogin());
        userNotificationEventOptionsRepository.deleteAll();
        //Creates update data
        var updateUserNotificationOptionsDto = createUpdateUserNotificationOptionsDto();
        assertThrows(NotificationEventNotFoundException.class,
                () -> userProfileOptionsService.updateUserNotificationOptions(userEntity.getLogin(),
                        updateUserNotificationOptionsDto));
    }

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }
}
