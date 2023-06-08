package com.ecaservice.common.web.exception;

import com.ecaservice.common.web.error.CommonErrorCode;

/**
 * Entity not found exception class.
 *
 * @author Roman Batygin
 */
public class EntityNotFoundException extends ValidationErrorException {

    /**
     * Creates entity not found exception.
     *
     * @param clazz     - entity class
     * @param searchKey - search key
     */
    public EntityNotFoundException(Class<?> clazz, Object searchKey) {
        super(CommonErrorCode.DATA_NOT_FOUND,
                String.format("Entity [%s] with search key [%s] not found!", clazz.getSimpleName(), searchKey));
    }
}
