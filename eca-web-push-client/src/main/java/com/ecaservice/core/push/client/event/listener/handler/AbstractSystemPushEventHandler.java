package com.ecaservice.core.push.client.event.listener.handler;

import com.ecaservice.core.push.client.event.model.AbstractSystemPushEvent;
import com.ecaservice.web.push.dto.SystemPushRequest;

/**
 * Abstract system push event handler.
 *
 * @param <E> - push request generic type
 * @author Roman Batygin
 */
public abstract class AbstractSystemPushEventHandler<E extends AbstractSystemPushEvent>
        extends AbstractPushEventHandler<E, SystemPushRequest> {

    protected AbstractSystemPushEventHandler(Class<E> clazz) {
        super(clazz);
    }

    @Override
    protected SystemPushRequest internalCreatePushRequest(E event) {
        var systemPushRequest = new SystemPushRequest();
        systemPushRequest.setShowMessage(isShowMessage());
        return systemPushRequest;
    }

    /**
     * Returns show message flag.
     *
     * @return show message flag
     */
    protected boolean isShowMessage() {
        return false;
    }
}
