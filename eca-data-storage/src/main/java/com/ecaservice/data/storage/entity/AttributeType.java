package com.ecaservice.data.storage.entity;

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

    private final String description;
}
