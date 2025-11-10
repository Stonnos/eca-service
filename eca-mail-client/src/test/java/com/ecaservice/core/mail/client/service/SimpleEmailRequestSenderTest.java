package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.mail.client.config.EcaMailClientProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static com.ecaservice.core.mail.client.TestHelperUtils.createEmailRequest;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SimpleEmailRequestSender} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class SimpleEmailRequestSenderTest {

    private static final String QUEUE_NAME = "queue";
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private EcaMailClientProperties ecaMailClientProperties;

    @InjectMocks
    private SimpleEmailRequestSender simpleEmailRequestSender;

    @Test
    void testHandleEmailEvent() {
        var emailRequest = createEmailRequest();
        EcaMailClientProperties.RabbitProperties properties = new EcaMailClientProperties.RabbitProperties();
        properties.setQueueName(QUEUE_NAME);
        when(ecaMailClientProperties.getRabbit()).thenReturn(properties);
        simpleEmailRequestSender.sendEmail(emailRequest);
        verify(rabbitTemplate, atLeastOnce()).convertAndSend(properties.getQueueName(), emailRequest);
    }
}
