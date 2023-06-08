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
    NUMERIC("Числовой") {
        @Override
        public <T> T handle(AttributeTypeVisitor<T> attributeTypeVisitor) {
            return attributeTypeVisitor.caseNumeric();
        }
    },

    /**
     * Nominal type
     */
    NOMINAL("Категориальный") {
        @Override
        public <T> T handle(AttributeTypeVisitor<T> attributeTypeVisitor) {
            return attributeTypeVisitor.caseNominal();
        }
    },

    /**
     * Date type
     */
    DATE("Дата и время") {
        @Override
        public <T> T handle(AttributeTypeVisitor<T> attributeTypeVisitor) {
            return attributeTypeVisitor.caseDate();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param attributeTypeVisitor visitor class
     * @param <T>                  generic class
     * @return generic class
     */
    public abstract <T> T handle(AttributeTypeVisitor<T> attributeTypeVisitor);

    private final String description;
}
