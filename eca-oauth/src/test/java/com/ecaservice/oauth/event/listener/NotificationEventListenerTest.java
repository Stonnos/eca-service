package com.ecaservice.oauth.event.listener;

import com.ecaservice.core.mail.client.event.model.EmailEvent;
import com.ecaservice.oauth.NotificationEventTestDataProvider;
import com.ecaservice.oauth.config.AppProperties;
import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.event.listener.handler.AbstractNotificationEventHandler;
import com.ecaservice.oauth.event.model.AbstractNotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link NotificationEventListener} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@ComponentScan(basePackageClasses = AbstractNotificationEventHandler.class)
@Import({NotificationEventListener.class, AppProperties.class, TfaConfig.class})
class NotificationEventListenerTest {

    @Inject
    private List<AbstractNotificationEventHandler> notificationEventHandlers;

    private ApplicationEventPublisher applicationEventPublisher;

    private NotificationEventListener notificationEventListener;

    @Captor
    private ArgumentCaptor<EmailEvent> emailRequestArgumentCaptor;

    @BeforeEach
    void init() {
        applicationEventPublisher = mock(ApplicationEventPublisher.class);
        notificationEventListener = new NotificationEventListener(applicationEventPublisher, notificationEventHandlers);
    }

    @ParameterizedTest(name = "Notification event template template code {1}")
    @ArgumentsSource(NotificationEventTestDataProvider.class)
    void testNotificationEvent(AbstractNotificationEvent event, String expectedTemplateCode, String expectedReceiver) {
        notificationEventListener.handleNotificationEvent(event);
        verify(applicationEventPublisher, atLeastOnce()).publishEvent(emailRequestArgumentCaptor.capture());
        EmailEvent actual = emailRequestArgumentCaptor.getValue();
        assertThat(actual).isNotNull();
        assertThat(actual.getEmailRequest()).isNotNull();
        assertThat(actual.getEmailRequest().getTemplateCode()).isEqualTo(expectedTemplateCode);
        assertThat(actual.getEmailRequest().getReceiver()).isEqualTo(expectedReceiver);
    }

    @Test
    void testThrowIllegalStateException() {
        var event = new AbstractNotificationEvent(this) {
            @Override
            public String getReceiver() {
                return null;
            }
        };
        assertThrows(IllegalStateException.class, () -> notificationEventListener.handleNotificationEvent(event));
    }
}
