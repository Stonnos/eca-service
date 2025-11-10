package com.ecaservice.server.mq.listener.support;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.server.exception.MessageAuthorizationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.util.Utils.buildErrorResponse;

/**
 * Message authorization exception handler.
 *
 * @author Roman Batygin
 */
@Component
@ConditionalOnProperty(value = "rabbit.enabled", havingValue = "true")
public class MessageAuthorizationTranslator extends AbstractExceptionTranslator<MessageAuthorizationException> {

    /**
     * Default constructor.
     */
    public MessageAuthorizationTranslator() {
        super(MessageAuthorizationException.class);
    }

    @Override
    public EcaResponse translate(MessageAuthorizationException exception) {
        return buildErrorResponse(ErrorCode.UNAUTHORIZED);
    }
}
