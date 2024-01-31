package com.ecaservice.server.event.listener;

import com.ecaservice.core.push.client.event.listener.WebPushEventListener;
import com.ecaservice.core.push.client.service.WebPushSender;
import com.ecaservice.core.push.client.validator.UserNotificationPushRequestValidator;
import com.ecaservice.server.event.model.push.SetActiveClassifiersConfigurationPushEvent;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationActionType;
import com.ecaservice.server.repository.ClassifiersConfigurationHistoryRepository;
import com.ecaservice.server.repository.ClassifiersConfigurationRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import com.ecaservice.server.service.push.handler.AddClassifierOptionsPushEventHandler;
import com.ecaservice.server.service.push.handler.SetActiveClassifiersConfigurationPushEventHandler;
import com.ecaservice.user.profile.options.client.service.UserProfileOptionsProvider;
import com.ecaservice.user.profile.options.dto.UserNotificationEventOptionsDto;
import com.ecaservice.user.profile.options.dto.UserNotificationEventType;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import com.ecaservice.web.push.dto.AbstractPushRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.server.TestHelperUtils.createClassifiersConfiguration;
import static com.ecaservice.server.TestHelperUtils.createClassifiersConfigurationHistory;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AddClassifierOptionsPushEventHandler} class.
 *
 * @author Roman Batygin
 */
@Import(SetActiveClassifiersConfigurationPushEventHandler.class)
class WebPushListenerTest extends AbstractJpaTest {

    private static final String CURRENT_USER = "currentUser";
    private static final String USER_1 = "user1";

    @MockBean
    private MessageTemplateProcessor messageTemplateProcessor;
    @MockBean
    private WebPushSender webPushSender;
    @MockBean
    private UserProfileOptionsProvider userProfileOptionsProvider;

    @Inject
    private ClassifiersConfigurationRepository classifiersConfigurationRepository;
    @Inject
    private ClassifiersConfigurationHistoryRepository classifiersConfigurationHistoryRepository;

    @Inject
    private SetActiveClassifiersConfigurationPushEventHandler setActiveClassifiersConfigurationPushEventHandler;

    private ClassifiersConfiguration classifiersConfiguration;

    private WebPushEventListener webPushEventListener;

    @Override
    public void deleteAll() {
        classifiersConfigurationHistoryRepository.deleteAll();
        classifiersConfigurationRepository.deleteAll();
    }

    @Override
    public void init() {
        saveConfiguration();
        webPushEventListener = new WebPushEventListener(
                Collections.singletonList(setActiveClassifiersConfigurationPushEventHandler),
                Collections.singletonList(new UserNotificationPushRequestValidator()), webPushSender);
    }

    @Test
    void testHandlePushEventWithEmptyClassifiersConfigurationHistory() {
        var event = new SetActiveClassifiersConfigurationPushEvent(this, CURRENT_USER, classifiersConfiguration);
        webPushEventListener.handlePushEvent(event);
        verify(webPushSender, never()).send(any(AbstractPushRequest.class));
    }

    @Test
    void testHandlePushEventWithNotEmptyClassifiersConfigurationHistoryAndEnabledPushNotifications() {
        saveHistory();
        mockGetUserProfileOptions(true);
        var event = new SetActiveClassifiersConfigurationPushEvent(this, CURRENT_USER, classifiersConfiguration);
        webPushEventListener.handlePushEvent(event);
        verify(webPushSender, atLeastOnce()).send(any(AbstractPushRequest.class));
    }

    @Test
    void testHandlePushEventWithNotEmptyClassifiersConfigurationHistoryAndDisabledPushNotifications() {
        saveHistory();
        mockGetUserProfileOptions(false);
        var event = new SetActiveClassifiersConfigurationPushEvent(this, CURRENT_USER, classifiersConfiguration);
        webPushEventListener.handlePushEvent(event);
        verify(webPushSender, never()).send(any(AbstractPushRequest.class));
    }

    private void saveConfiguration() {
        ClassifiersConfiguration configuration = createClassifiersConfiguration();
        configuration.setCreatedBy(CURRENT_USER);
        classifiersConfiguration = classifiersConfigurationRepository.save(configuration);
    }

    private void mockGetUserProfileOptions(boolean webPushEnabled) {
        UserProfileOptionsDto userProfileOptionsDto = new UserProfileOptionsDto();
        userProfileOptionsDto.setWebPushEnabled(webPushEnabled);
        UserNotificationEventOptionsDto userNotificationEventOptionsDto = new UserNotificationEventOptionsDto();
        userNotificationEventOptionsDto.setEventType(UserNotificationEventType.CLASSIFIER_CONFIGURATION_CHANGE);
        userNotificationEventOptionsDto.setWebPushEnabled(webPushEnabled);
        userProfileOptionsDto.setNotificationEventOptions(Collections.singletonList(userNotificationEventOptionsDto));
        when(userProfileOptionsProvider.getUserProfileOptions(anyString())).thenReturn(userProfileOptionsDto);
    }

    private void saveHistory() {
        var history = List.of(
                createClassifiersConfigurationHistory(classifiersConfiguration,
                        ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS, USER_1),
                createClassifiersConfigurationHistory(classifiersConfiguration,
                        ClassifiersConfigurationActionType.ADD_CLASSIFIER_OPTIONS, CURRENT_USER)
        );
        classifiersConfigurationHistoryRepository.saveAll(history);
    }
}
