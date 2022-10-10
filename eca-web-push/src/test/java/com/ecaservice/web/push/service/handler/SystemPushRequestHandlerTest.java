package com.ecaservice.web.push.service.handler;

import com.ecaservice.web.dto.model.push.PushRequestDto;
import com.ecaservice.web.push.config.ws.QueueConfig;
import com.ecaservice.web.push.controller.api.WebPushController;
import com.ecaservice.web.push.mapping.NotificationMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.web.push.TestHelperUtils.createSystemPushRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link WebPushController} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({QueueConfig.class, SystemPushNotificationRequestHandler.class, NotificationMapperImpl.class})
class SystemPushRequestHandlerTest {

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Inject
    private QueueConfig queueConfig;
    @Inject
    private SystemPushNotificationRequestHandler systemPushNotificationRequestHandler;

    @Captor
    private ArgumentCaptor<String> destinationCaptor;

    @Test
    void testHandleSystemPush() {
        var systemPushRequest = createSystemPushRequest();
        systemPushNotificationRequestHandler.handle(systemPushRequest);
        verify(messagingTemplate, atLeastOnce()).convertAndSend(destinationCaptor.capture(), any(PushRequestDto.class));
        assertThat(destinationCaptor.getValue()).isNotNull();
        assertThat(destinationCaptor.getValue()).isEqualTo(queueConfig.getPushQueue());
    }
}
