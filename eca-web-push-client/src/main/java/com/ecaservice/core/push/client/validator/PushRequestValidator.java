package com.ecaservice.core.push.client.validator;

import com.ecaservice.web.push.dto.AbstractPushRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Abstract push request validator.
 *
 * @param <R> - push event generic type
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PushRequestValidator<R extends AbstractPushRequest> {

    private final Class<R> clazz;

    /**
     * Can handle push request?
     *
     * @param pushRequest - push request
     * @return {@code true} if can handle push request, otherwise {@code false}
     */
    public boolean canHandle(AbstractPushRequest pushRequest) {
        return pushRequest != null && clazz.isAssignableFrom(pushRequest.getClass());
    }

    /**
     * Is valid push event?
     *
     * @param pushRequest - push request
     * @return {@code true} if push request is valid, otherwise {@code false}
     */
    public abstract boolean isValid(R pushRequest);
}
