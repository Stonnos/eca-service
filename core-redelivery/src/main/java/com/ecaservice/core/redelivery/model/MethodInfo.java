package com.ecaservice.core.redelivery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * Method info.
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodInfo {

    /**
     * Spring bean object
     */
    private Object bean;

    /**
     * Method
     */
    private Method method;
}
