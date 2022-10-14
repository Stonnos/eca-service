package com.ecaservice.web.push.service.handler;

import com.ecaservice.web.push.dto.AbstractPushRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Abstract push request handler.
 *
 * @param <R> - request generic type
 * @author Roman Batygin
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractPushRequestHandler<R extends AbstractPushRequest> {

    private final Class<R> clazz;

    /**
     * Can handle push request?
     *
     * @param abstractPushRequest - push request
     * @return {@code true} if request can handle, otherwise {@code false}
     */
    public boolean canHandle(AbstractPushRequest abstractPushRequest) {
        return abstractPushRequest != null && clazz.isAssignableFrom(abstractPushRequest.getClass());
    }

    /**
     * Handler push request.
     *
     * @param request - push request
     */
    public abstract void handle(R request);
}
