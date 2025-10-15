package com.ecaservice.server.mq.listener;

import com.ecaservice.server.exception.MessageAuthorizationException;
import com.ecaservice.server.service.auth.PersonalAccessTokensClient;
import com.ecaservice.user.dto.PersonalAccessTokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.mq.listener.MessageHeaders.AUTH_TOKEN_HEADER;
import static com.ecaservice.server.mq.listener.MessageHeaders.USER_HEADER;

/**
 * Authorize message post processor.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "rabbit.enabled", havingValue = "true")
@RequiredArgsConstructor
public class AuthorizeMessagePostProcessor implements MessagePostProcessor {

    private final PersonalAccessTokensClient personalAccessTokensClient;

    @Override
    public Message postProcessMessage(Message message) {
        log.debug("Authorize message with correlation id [{}]", message.getMessageProperties().getCorrelationId());
        String token = message.getMessageProperties().getHeader(AUTH_TOKEN_HEADER);
        if (StringUtils.isEmpty(token)) {
            throw new MessageAuthorizationException("Auth token must be specified!");
        }
        var tokenInfo = personalAccessTokensClient.verifyToken(token);
        if (!tokenInfo.isValid()) {
            throw new MessageAuthorizationException("Got invalid auth token!");
        }
        if (PersonalAccessTokenType.USER_TOKEN.equals(tokenInfo.getTokenType())) {
            message.getMessageProperties().setHeader(USER_HEADER, tokenInfo.getUser());
        }
        log.debug("Message with correlation id [{}] has been authorized",
                message.getMessageProperties().getCorrelationId());
        return message;
    }
}
