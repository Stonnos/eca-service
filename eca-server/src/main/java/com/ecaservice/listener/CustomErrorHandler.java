package com.ecaservice.listener;

import com.ecaservice.base.model.EcaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import static com.ecaservice.util.Utils.buildErrorResponse;

/**
 * Custom error handler implementation.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomErrorHandler implements ErrorHandler {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void handleError(Throwable ex) {
        if (ex instanceof ListenerExecutionFailedException) {
            ListenerExecutionFailedException failedException = (ListenerExecutionFailedException) ex;
            Message failedMessage = failedException.getFailedMessage();
            MessageProperties messageProperties = failedMessage.getMessageProperties();
            log.error("There was an error while message [{}] processing: {}", messageProperties.getCorrelationId(),
                    failedException.getCause().getMessage());
            EcaResponse errorResponse = buildErrorResponse(ex.getCause().getMessage());
            rabbitTemplate.convertAndSend(messageProperties.getReplyTo(), errorResponse, outboundMessage -> {
                outboundMessage.getMessageProperties().setCorrelationId(messageProperties.getCorrelationId());
                return outboundMessage;
            });
        } else {
            log.error("Unknown error while message handling: {}", ex.getCause().getMessage());
        }
    }
}
