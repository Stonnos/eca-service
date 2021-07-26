package com.ecaservice.common.web.exception;

/**
 * Entity not found exception class.
 *
 * @author Roman Batygin
 */
public class EntityNotFoundException extends ValidationErrorException {

    private static final String ERROR_CODE = "DataNotFound";

    /**
     * Creates entity not found exception.
     *
     * @param clazz     - entity class
     * @param searchKey - search key
     */
    public EntityNotFoundException(Class<?> clazz, Object searchKey) {
        super(ERROR_CODE,
                String.format("Entity [%s] with search key [%s] not found!", clazz.getSimpleName(), searchKey));
    }
}
