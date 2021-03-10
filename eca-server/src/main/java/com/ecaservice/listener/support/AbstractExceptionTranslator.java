package com.ecaservice.listener.support;

import com.ecaservice.base.model.EcaResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Abstract exception handler class.
 *
 * @param <T> - exception type
 * @author Roman Batygin
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractExceptionTranslator<T extends Throwable> {

    /**
     * Exception class.
     */
    @Getter
    private final Class<T> clazz;

    /**
     * Can translate exception?
     *
     * @param exception - exception object
     * @return {@code true} if class can translate exception
     */
    public boolean canTranslate(T exception) {
        return exception != null && clazz.isAssignableFrom(exception.getClass());
    }

    /**
     * Translates exception to eca response.
     *
     * @param exception - exception object
     * @return eca response
     */
    public abstract EcaResponse translate(T exception);
}
