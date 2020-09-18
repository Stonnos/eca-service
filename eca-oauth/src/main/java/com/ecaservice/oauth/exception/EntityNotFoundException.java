package com.ecaservice.oauth.exception;

import lombok.NoArgsConstructor;

/**
 * Entity not found exception class.
 *
 * @author Roman Batygin
 */
@NoArgsConstructor
public class EntityNotFoundException extends RuntimeException {

    /**
     * Creates entity not found exception.
     *
     * @param clazz    - entity class
     * @param searchKey - search key
     */
    public EntityNotFoundException(Class<?> clazz, Object searchKey) {
        super(String.format("Entity [%s] with search key [%s] not found!", clazz.getSimpleName(), searchKey));
    }
}