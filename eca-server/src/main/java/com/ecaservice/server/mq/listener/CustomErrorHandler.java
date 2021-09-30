package com.ecaservice.server.mq.listener;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.server.mq.listener.support.AbstractExceptionTranslator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import java.util.List;

import static com.ecaservice.server.util.Utils.buildErrorResponse;

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
    private final List<AbstractExceptionTranslator> exceptionTranslators;

    @Override
    public void handleError(Throwable ex) {
        if (ex instanceof ListenerExecutionFailedException) {
            ListenerExecutionFailedException failedException = (ListenerExecutionFailedException) ex;
            EcaResponse errorResponse = translate(failedException);
            Message failedMessage = failedException.getFailedMessage();
            MessageProperties messageProperties = failedMessage.getMessageProperties();
            log.error("There was an error while message [{}] processing: {}", messageProperties.getCorrelationId(),
                    errorResponse);
            rabbitTemplate.convertAndSend(messageProperties.getReplyTo(), errorResponse, outboundMessage -> {
                outboundMessage.getMessageProperties().setCorrelationId(messageProperties.getCorrelationId());
                return outboundMessage;
            });
        } else {
            log.error("Unknown error while message handling: {}", ex.getCause().getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private EcaResponse translate(ListenerExecutionFailedException ex) {
        Throwable cause = ex.getCause();
        return exceptionTranslators.stream()
                .filter(t -> t.canTranslate(cause))
                .findFirst()
                .map(t -> t.translate(cause))
                .orElse(buildErrorResponse(cause.getMessage()));
    }
}
