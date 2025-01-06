package com.ecaservice.core.transactional.outbox.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

/**
 * Method info.
 *
 * @author Roman Batygin
 */
@Data
@RequiredArgsConstructor
public class MethodInfo {

    /**
     * Spring bean object
     */
    private final Object bean;

    /**
     * Method
     */
    private final Method method;
}
