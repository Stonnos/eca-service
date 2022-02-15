package com.ecaservice.core.redelivery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * Данные о методе бина.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodInfo {

    private Object bean;

    private Method method;
}
