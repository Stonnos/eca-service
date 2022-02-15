package com.ecaservice.core.redelivery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Данные о методах бина.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodsInfo {

    private Object bean;

    private List<Method> methods;
}
