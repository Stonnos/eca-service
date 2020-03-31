package com.ecaservice.exception;

/**
 * Entity not found exception class.
 *
 * @author Roman Batygin
 */
public class EntityNotFoundException extends EcaServiceException {

    /**
     * Creates entity not found exception.
     *
     * @param clazz    - entity class
     * @param entityId - entity id
     */
    public EntityNotFoundException(Class<?> clazz, Long entityId) {
        super(String.format("Entity [%s] with id [%d] not found!", clazz.getSimpleName(), entityId));
    }
}