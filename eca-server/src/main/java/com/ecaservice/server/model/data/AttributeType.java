package com.ecaservice.server.model.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Attribute type enum.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum AttributeType {

    /**
     * Numeric type
     */
    NUMERIC("Числовой"),

    /**
     * Nominal type
     */
    NOMINAL("Категориальный"),

    /**
     * Date type
     */
    DATE("Дата и время");

    /**
     * Attribute description
     */
    private final String description;
}
