package com.ecaservice.core.push.client.event.listener.handler;

import com.ecaservice.core.push.client.event.model.AbstractPushEvent;
import com.ecaservice.web.push.dto.AbstractPushRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Abstract push event handler.
 *
 * @param <E> - push request generic type
 * @param <R> - push event generic type
 * @author Roman Batygin
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractPushEventHandler<E extends AbstractPushEvent, R extends AbstractPushRequest> {

    private final Class<E> clazz;

    /**
     * Can handle push event?
     *
     * @param pushEvent - push event
     * @return {@code true} if can handle push event, otherwise {@code false}
     */
    public boolean canHandle(AbstractPushEvent pushEvent) {
        return pushEvent != null && clazz.isAssignableFrom(pushEvent.getClass());
    }

    /**
     * Handles push event.
     *
     * @param event - push event
     * @return push request
     */
    public R handleEvent(E event) {
        var pushRequest = internalCreatePushRequest(event);
        pushRequest.setRequestId(UUID.randomUUID().toString());
        pushRequest.setMessageType(getMessageType(event));
        pushRequest.setMessageText(getMessageText(event));
        pushRequest.setAdditionalProperties(createAdditionalProperties(event));
        return pushRequest;
    }

    /**
     * Gets message type.
     *
     * @return message type
     */
    protected abstract String getMessageType(E event);

    /**
     * Gets message text.
     *
     * @param event - push event
     * @return message text
     */
    protected abstract String getMessageText(E event);

    /**
     * Creates additional properties map.
     *
     * @param event - push event
     * @return additional properties map
     */
    protected abstract Map<String, String> createAdditionalProperties(E event);

    /**
     * Creates push request with its specific properties.
     *
     * @param event - push event
     * @return push request
     */
    protected abstract R internalCreatePushRequest(E event);
}
