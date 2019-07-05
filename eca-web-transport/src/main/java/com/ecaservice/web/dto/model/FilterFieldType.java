package com.ecaservice.web.dto.model;

import com.ecaservice.web.dto.FilterFieldTypeVisitor;

/**
 * Field field type.
 *
 * @author Roman Batygin
 */
public enum FilterFieldType {

    /**
     * Text field filter
     */
    TEXT {
        @Override
        public <T> T handle(FilterFieldTypeVisitor<T> filterFieldTypeVisitor) {
            return filterFieldTypeVisitor.caseText();
        }
    },

    /**
     * Reference field filter
     */
    REFERENCE {
        @Override
        public <T> T handle(FilterFieldTypeVisitor<T> filterFieldTypeVisitor) {
            return filterFieldTypeVisitor.caseReference();
        }
    },

    /**
     * Date field filter in format yyyy-MM-dd
     */
    DATE {
        @Override
        public <T> T handle(FilterFieldTypeVisitor<T> filterFieldTypeVisitor) {
            return filterFieldTypeVisitor.caseDate();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param filterFieldTypeVisitor - filter field type visitor
     * @param <T>                    - generic type
     * @return generic object
     */
    public abstract <T> T handle(FilterFieldTypeVisitor<T> filterFieldTypeVisitor);
}
