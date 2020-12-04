package com.ecaservice.external.api.service;


import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.external.api.config.rabbit.QueueConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for checking {@link RabbitSender} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({RabbitSender.class, QueueConfig.class})
class RabbitSenderTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Inject
    private RabbitSender rabbitSender;

    @Captor
    private ArgumentCaptor<String> queueCaptor;

    @Test
    void testSendEvaluationRequest() {
        rabbitSender.sendEvaluationRequest(new EvaluationRequest(), UUID.randomUUID().toString());
        verify(rabbitTemplate).convertAndSend(queueCaptor.capture(), any(EvaluationRequest.class), any(
                MessagePostProcessor.class));
    }
}
