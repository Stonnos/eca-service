package com.ecaservice.common.web.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Entity not found exception class.
 *
 * @author Roman Batygin
 */
@NoArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Entity not found")
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
